package com.programyourhome.ir.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * The reply of the WinLIRC server is structured as follows:
 * - All seperate parts of the reply message are put on a new line.
 * - A reply always has the following structure:
 *   - it starts with BEGIN,
 *   - then a copy of the command received,
 *   - then a status indicator (SUCCESS or ERROR),
 *   - then, optionally a data section,
 *   - finally, it ends with END.
 * - When the reply contains extra information about the command result or the error, a data section is present.
 * - The data section has the following structure:
 *   - it starts with DATA,
 *   - then the number of data lines that will follow,
 *   - then the actual data lines.
 * - In case of an error, there will typically be 1 data line with the error message.
 * - In case of a successful LIST command, the data lines will contain the result of the listing (remote or key names).
 * 
 * 
 * The description above is summarized in the following grammar description of a WinLIRC server reply.
 * 
 * Reply:
 *   BEGIN Newline Content Newline END
 * Content:
 *   CommandLine Newline Status Newline [Data]
 * CommandLine:
 *   .*
 * Status:
 *   SUCCESS | ERROR
 * Data:
 *  DATA Newline NumberOfDataLines Newline (DataLine Newline){NumberOfDataLines}
 * NumberOfDataLines:
 *   [0-9]+
 * DataLine:
 *   .*
 * Newline:
 *   \n
 * 
 * Note: A known exception to this is when WinLIRC has too many connected clients (64 is the limit).
 * Then it will reply with: "Sorry the server is full."
 * </pre>
 */
// TODO: slightly weird that this is not an Impl class, since there is no corresponding interface at the api side.
// TODO: Change naming convention for Impl classes?
// TODO: This screams for some unit tests!
public class ServerReply {

    public static final String REPLY_BEGIN = "BEGIN";
    public static final String REPLY_SUCCESS = "SUCCESS";
    public static final String REPLY_ERROR = "ERROR";
    public static final String REPLY_DATA = "DATA";
    public static final String REPLY_END = "END";

    private final String commandReceived;
    private final boolean success;
    private final List<String> data;

    private ServerReply(final String commandReceived, final boolean success, final List<String> data) {
        this.commandReceived = commandReceived;
        this.success = success;
        this.data = data;
    }

    public String getCommandReceived() {
        return this.commandReceived;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public List<String> getData() {
        return this.data;
    }

    // TODO: beautify a little more, replacing 'magic numbers' by constants
    public static ServerReply parse(final List<String> lines) throws IllegalArgumentException {
        if (lines.size() < 4) {
            System.out.println("Server reply: " + lines);
            throw new IllegalArgumentException("Server reply has " + lines.size() + " lines, while the minimum valid reply is 4 lines long.");
        }
        if (!lines.get(0).equals(REPLY_BEGIN)) {
            throw new IllegalArgumentException("Server reply did not start with '" + REPLY_BEGIN + "'.");
        }
        final String commandReceived = lines.get(1);
        final String status = lines.get(2);
        final List<String> data = new ArrayList<>();
        boolean success;
        if (status.equals(REPLY_SUCCESS)) {
            success = true;
        } else if (status.equals(REPLY_ERROR)) {
            success = false;
        } else {
            throw new IllegalArgumentException("Invalid status line: '" + status + "', either '" + REPLY_SUCCESS + "' or '" + REPLY_ERROR + "' expected.");
        }
        final String dataOrEnd = lines.get(3);
        if (dataOrEnd.equals(REPLY_DATA)) {
            if (lines.size() < 7) {
                throw new IllegalArgumentException("Server reply with data has " + lines.size()
                        + " lines, while the minimum valid reply with data is 7 lines long.");
            }
            final String numberOfDataLinesString = lines.get(4);
            if (!StringUtils.isNumeric(numberOfDataLinesString)) {
                throw new IllegalArgumentException("Non-numeric number of data lines: '" + numberOfDataLinesString + "'.");
            }
            final int numberOfDataLines = Integer.parseInt(numberOfDataLinesString);
            final int expectedTotalNumberOfLines = 5 + numberOfDataLines + 1;
            if (lines.size() != expectedTotalNumberOfLines) {
                throw new IllegalArgumentException("Server reply has " + lines.size() + " lines, "
                        + "while the expected size, based on the number of data lines, is " + expectedTotalNumberOfLines + ".");
            }
            data.addAll(lines.stream()
                    .skip(5)
                    .limit(numberOfDataLines)
                    .collect(Collectors.toList()));
            if (!lines.get(lines.size() - 1).equals(REPLY_END)) {
                throw new IllegalArgumentException("Server reply did not end with '" + REPLY_END + "'.");
            }
        } else if (dataOrEnd.equals(REPLY_END)) {
            // Done, reply closed successfully.
        } else {
            throw new IllegalArgumentException("Invalid 4th line: '" + dataOrEnd + "', either '" + REPLY_DATA + "' or '" + REPLY_END + "' expected.");
        }
        return new ServerReply(commandReceived, success, data);
    }
}

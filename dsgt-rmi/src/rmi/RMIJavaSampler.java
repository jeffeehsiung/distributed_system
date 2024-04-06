package rmi;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import staff.BookingClient;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class RMIJavaSampler extends AbstractJavaSamplerClient {
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        String serverUrl = context.getParameter("SERVER_URL");
        int portNumber = Integer.parseInt(context.getParameter("PORT_NUMBER")); // Parse port as integer

        // Prepare a stream to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out; // Keep the original System.out
        System.setOut(new PrintStream(outContent)); // Redirect System.out

        result.sampleStart(); // Start the stopwatch

        try {
            // Initialize your BookingClient and perform operations
            BookingClient client = new BookingClient(serverUrl, portNumber);
            client.startLoadTest(); // Assume this is the method you want to test

            result.sampleEnd(); // Stop the stopwatch
            result.setSuccessful(true);
            result.setResponseMessage("Successfully performed RMI operation.");
            result.setResponseCodeOK(); // 200 code

            // Set the captured output as the response data
            result.setResponseData(outContent.toString(), "UTF-8");
        } catch (Exception e) {
            result.sampleEnd(); // Stop the stopwatch
            result.setSuccessful(false);
            result.setResponseMessage("Exception: " + e.getMessage());
            // Optionally, log the stack trace or handle the error as needed
        } finally {
            System.setOut(originalOut); // Reset System.out
        }

        return result;
    }
}

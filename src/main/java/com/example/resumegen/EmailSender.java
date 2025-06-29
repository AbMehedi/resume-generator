package com.example.resumegen;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class EmailSender {
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final int SMTP_PORT = 465;
    private static final String USERNAME = "resumebuilderbeta@gmail.com"; // Replace with your email
    private static final String PASSWORD = "itieijczisaxkmem";   // Replace with your app password


    public static void sendEmailWithAttachment(
            String recipient,
            String subject,
            String body,
            Path attachmentPath
    ) throws IOException {
        System.out.println("Starting email sending process...");
        System.out.println("Using SMTPS (port 465) with implicit SSL...");

        SSLSocket sslSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            // Step 1: Establish SSL connection directly
            System.out.println("Creating SSL socket to " + SMTP_SERVER + ":" + SMTP_PORT);
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslSocket = (SSLSocket) sslSocketFactory.createSocket(SMTP_SERVER, SMTP_PORT);

            // Enable all supported cipher suites
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());

            // Start handshake
            System.out.println("Starting SSL handshake...");
            sslSocket.startHandshake();
            System.out.println("SSL handshake completed.");

            // Get streams
            in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            out = new PrintWriter(sslSocket.getOutputStream(), true);

            // Read server greeting
            System.out.println("Reading server greeting...");
            String response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            // Start SMTP conversation
            System.out.println("Sending EHLO...");
            sendCommand(out, "EHLO localhost");
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            // Read all EHLO response lines
            while (response != null && response.startsWith("250-")) {
                response = in.readLine();
                System.out.println("S: " + response);
                checkResponse(response);
            }

            System.out.println("Authenticating...");
            sendCommand(out, "AUTH LOGIN");
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            // Send username
            String usernameB64 = Base64.getEncoder().encodeToString(USERNAME.getBytes());
            sendCommand(out, usernameB64);
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            // Send password
            String passwordB64 = Base64.getEncoder().encodeToString(PASSWORD.getBytes());
            sendCommand(out, passwordB64);
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            System.out.println("Setting sender and recipient...");
            sendCommand(out, "MAIL FROM: <" + USERNAME + ">");
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            sendCommand(out, "RCPT TO: <" + recipient + ">");
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            sendCommand(out, "DATA");
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            // Build MIME message
            System.out.println("Constructing email...");
            String boundary = "boundary_" + System.currentTimeMillis();

            // Write headers directly
            out.println("MIME-Version: 1.0");
            out.println("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"");
            out.println("Subject: " + subject);
            out.println("From: " + USERNAME);
            out.println("To: " + recipient);
            out.println(); // Empty line to separate headers from body

            // Text body part
            out.println("--" + boundary);
            out.println("Content-Type: text/plain; charset=UTF-8");
            out.println(); // Empty line to separate headers from content
            out.println(body);
            out.println(); // Empty line after content

            // Attachment part
            out.println("--" + boundary);
            out.println("Content-Type: application/pdf");
            out.println("Content-Transfer-Encoding: base64");
            out.println("Content-Disposition: attachment; filename=\"resume.pdf\"");
            out.println(); // Empty line to separate headers from content
            encodeFileToBase64(attachmentPath, out);
            out.println(); // Empty line after content

            // End message
            out.println("--" + boundary + "--");
            out.println(); // Empty line after boundary

            // End of DATA command
            out.println(".");
            out.flush(); // Important to flush the buffer

            // Read server response after sending data
            response = in.readLine();
            System.out.println("S: " + response);
            checkResponse(response);

            sendCommand(out, "QUIT");
            response = in.readLine();
            System.out.println("S: " + response);

            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Email sending failed: " + e.getMessage(), e);
        } finally {
            // Clean up resources
            closeQuietly(out);
            closeQuietly(in);
            closeQuietly(sslSocket);
        }
    }

    private static void sendCommand(PrintWriter out, String command) {
        System.out.println("C: " + command);
        out.println(command);
    }

    private static void checkResponse(String response) throws IOException {
        if (response == null) {
            throw new IOException("Server closed connection unexpectedly");
        }

        // Check for error responses (4xx or 5xx)
        if (response.startsWith("4") || response.startsWith("5")) {
            throw new IOException("SMTP error: " + response);
        }
    }

    private static void encodeFileToBase64(Path filePath, PrintWriter out) throws IOException {
        System.out.println("Encoding attachment...");
        byte[] fileContent = Files.readAllBytes(filePath);
        String encoded = Base64.getEncoder().encodeToString(fileContent);

        // Split into lines of 76 characters
        int chunkSize = 76;
        for (int i = 0; i < encoded.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, encoded.length());
            out.println(encoded.substring(i, end));
        }
        System.out.println("Attachment encoded and sent.");
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }

    private static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}

//  -Djavax.net.ssl.trustStoreType=WINDOWS-ROOT -Djavax.net.ssl.trustStore=NONE
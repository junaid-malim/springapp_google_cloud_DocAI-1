package com.junaid.QuickStart;

import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class QuickStart {
    public static void main(String[] args)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // TODO(developer): Replace these variables before running the sample.
        String projectId = "docs-demo-is";
        String location = "us";
        String processorId = "c7534d9832c3633e";
        String filePath = "C:\\Sample.pdf";
        quickStart(projectId, location, processorId, filePath);
    }

    public static void quickStart(
            String projectId, String location, String processorId, String filePath)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {

        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create()) {

            String name =
                    String.format("projects/%s/locations/%s/processors/%s", projectId, location, processorId);

            byte[] imageFileData = Files.readAllBytes(Paths.get(filePath));

            ByteString content = ByteString.copyFrom(imageFileData);

            RawDocument document =
                    RawDocument.newBuilder().setContent(content).setMimeType("application/pdf").build();

            ProcessRequest request =
                    ProcessRequest.newBuilder().setName(name).setRawDocument(document).build();

            ProcessResponse result = client.processDocument(request);
            Document documentResponse = result.getDocument();

            String text = documentResponse.getText();

            System.out.println("The document contains the following paragraphs:");
            Document.Page firstPage = documentResponse.getPages(0);
            List<Document.Page.Paragraph> paragraphs = firstPage.getParagraphsList();

            for (Document.Page.Paragraph paragraph : paragraphs) {
                String paragraphText = getText(paragraph.getLayout().getTextAnchor(), text);
                System.out.printf("Paragraph text:\n%s\n", paragraphText);
            }
        }
    }

    private static String getText(Document.TextAnchor textAnchor, String text) {
        if (textAnchor.getTextSegmentsList().size() > 0) {
            int startIdx = (int) textAnchor.getTextSegments(0).getStartIndex();
            int endIdx = (int) textAnchor.getTextSegments(0).getEndIndex();
            return text.substring(startIdx, endIdx);
        }
        return "[NO TEXT]";
    }
}
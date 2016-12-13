package br.com.silva.Tools;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import br.com.silva.model.CA;
import br.com.silva.resources.MongoResource;

public class PDFImporter {

	private static final String CA_FOLDER = "/home/pulu/Documents/CAs";
	private static MongoCollection<Document> caPdfCollection = MongoResource.getDataBase("ca").getCollection("capdf");
	private static MongoCollection<Document> caStatusCollection = MongoResource.getDataBase("ca")
			.getCollection("castatus");

	public static void main(String[] args) {
		long beginCA = new Date().getTime();
		List<String> files = new ArrayList<String>();
		try (Stream<Path> paths = Files.walk(Paths.get(CA_FOLDER))) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					files.add(filePath.toString());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("There are " + files.size() + " files available.");
		for (String file : files) {
			try {
				CA ca = CAReader.readPDF(file);

				caPdfCollection.insertOne(CAParser.toDocument(ca).append("fileName", file));

				caStatusCollection
						.updateOne(
								eq("number", ca.getNumber()), combine(set("number", ca.getNumber()), set("exist", true),
										set("downloaded", true), set("imported", true)),
								new UpdateOptions().upsert(true));
			} catch (Exception e) {
				int end = file.indexOf("_") == -1 ? file.indexOf(".pdf") : file.indexOf("_");
				int number = Integer.parseInt(file.substring(CA_FOLDER.length() + 1, end));
				caStatusCollection.updateOne(
						eq("number", number), combine(set("number", number), set("exist", true),
								set("downloaded", true), set("imported", false), set("retry", true)),
						new UpdateOptions().upsert(true));
			}

		}
		System.out.println("Operation finished in " + (new Date().getTime() - beginCA) + "ms");
	}
}
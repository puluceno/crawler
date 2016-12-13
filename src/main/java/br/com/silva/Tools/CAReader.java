package br.com.silva.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import br.com.silva.model.CA;
import br.com.silva.model.Report;

public class CAReader {

	private static String LN = System.getProperty("line.separator");

	// 20254_23032012, 18631_31122011, 21153_08072016, 18629_31122011,
	// 18630_31122011
	public static CA readPDF(String pathToPDF) throws Exception {
		// public static void main(String[] args) {
		CA ca = new CA();

		try {
			// PdfReader reader = new
			// PdfReader("/home/pulu/Documents/CAs/33429_02032015.pdf");
			PdfReader reader = new PdfReader(pathToPDF);
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				String page = PdfTextExtractor.getTextFromPage(reader, i);

				boolean hasDate = page.contains(LN + "Validade:");
				boolean hasOrigin = page.contains(LN + "Produto:");
				boolean hasEquipment = page.contains(LN + "Equipamento: ");
				boolean hasDescription = page.contains(LN + "Descrição:");
				boolean hasApprovedFor = page.contains(LN + "Aprovado para:");
				boolean hasCaLocation = page.contains(LN + "Marcação do CA:");
				boolean hasReferences = page.contains(LN + "Referências:");
				boolean hasRestrictions = page.contains(LN + "Restrições/Limitações:");
				boolean hasObservation = page.contains(LN + "Observação:");
				boolean hasTechnician = page.contains(LN + "Responsável Técnico:");
				boolean hasColors = page.contains("Cores:");
				boolean hasSize = page.contains(LN + "Tamanhos:");
				boolean hasProfessionalRegistration = page.contains("Nº Registro Profissional:");
				boolean hasInmetroSticker = page.contains(LN + "Marcação do selo do Inmetro:");
				boolean hasInmetroConformityProof = page.contains(LN + "Atestado de conformidade Inmetro:");
				boolean hasTechRules = page.contains(LN + "Normas técnicas:");
				boolean hasReports = page.contains("Nº. Laudo: ");
				boolean hasCompany = page.contains(LN + "Empresa:");
				boolean hasCNPJ = page.contains(LN + "CNPJ:");
				boolean hasAttenuationTable = page.contains("Tabela de Atenuação");

				String[] line = page.split(LN + "");

				if (page.contains("CERTIFICADO DE APROVAÇÃO - CA Nº")) {
					int number = Integer.parseInt(
							page.substring(page.indexOf("Nº") + 3, page.indexOf("Nº") + 10).replaceAll("[^\\d]", ""));
					ca.setNumber(number);
				}

				String date = "";
				String processNumber = "";

				if (hasDate) {
					date = page.substring(page.indexOf("Validade:") + 9, page.indexOf("Nº. do Processo: ") - 1);
					processNumber = page.substring(page.indexOf("Nº. do Processo: ") + 17,
							page.indexOf("Produto: ") - 1);
					ca.setDate(removeNewLine(date));
					ca.setProcessNumber(removeNewLine(processNumber));

					String status = line[6].trim();
					ca.setStatus(status.isEmpty() ? date : status);
				}

				if (hasOrigin) {
					String origin = page.substring(page.indexOf("Produto: ") + 9, page.indexOf("Equipamento: ") - 1);
					ca.setOrigin(removeNewLine(origin));
				}

				if (hasEquipment) {
					String equipment = page.substring(page.indexOf("Equipamento: ") + 13,
							page.indexOf("Descrição:") - 1);
					ca.setEquipment(removeNewLine(equipment));
				}

				if (hasAttenuationTable) {
					Map<String, List<String>> attenuationTable = new HashMap<String, List<String>>();

					String[] freqs = page
							.substring(page.indexOf("Frequência (Hz): ") + 17, page.indexOf("Atenuação db:") - 1)
							.split(" ");
					LinkedList<String> frequencies = new LinkedList<String>(Arrays.asList(freqs));

					String[] dbs = page
							.substring(page.indexOf("Atenuação db:") + 13, page.indexOf("Desvio Padrão:") - 1)
							.split(" ");
					LinkedList<String> dbAttenuations = new LinkedList<String>(Arrays.asList(dbs));

					String[] devs = page.substring(page.indexOf("Desvio Padrão:") + 14, page.lastIndexOf(LN))
							.split(" ");
					LinkedList<String> deviations = new LinkedList<String>(Arrays.asList(devs));

					attenuationTable.put("frequencies", frequencies);
					attenuationTable.put("dbAttenuations", dbAttenuations);
					attenuationTable.put("deviations", deviations);
					ca.setAttenuationTable(attenuationTable);
				}

				if (hasCompany) {
					String company = page.substring(page.indexOf("Empresa: ") + 9, page.indexOf("CNPJ:") - 1);
					if (company.contains("CNAE:"))
						company = company.substring(0, company.indexOf("CNAE:"));
					ca.setCompany(removeNewLine(company));
				}

				if (hasCNPJ) {
					String cnpj = page.substring(page.indexOf("CNPJ: ") + 6, page.indexOf("CNPJ:") + 24);
					ca.setCnpj(removeNewLine(cnpj));
				}

				if (hasDescription && hasApprovedFor) {
					String description = page.substring(page.indexOf("Descrição:") + 10,
							page.indexOf(LN + "Aprovado para:"));
					ca.setDescription(removeNewLine(description));
				}

				if (hasApprovedFor) {
					String approvedFor = page.substring(page.indexOf("Aprovado para: ") + 15,
							page.indexOf("Restrições/Limitações: ") == -1
									? (page.indexOf("Observação: ") == -1 ? page.indexOf("Marcação do CA:") - 1
											: page.indexOf("Observação: ") - 1)
									: page.indexOf("Restrições/Limitações: "));
					ca.setApprovedFor(removeNewLine(approvedFor));
				} else {
					ca.setApprovedFor("CA não contém este campo, portanto é irrelevante");
					return ca;
					// TODO: put it back
				}

				if (hasRestrictions) {
					String restrictions = page.substring(page.indexOf("Restrições/Limitações: ") + 23,
							hasObservation ? page.indexOf("Observação: ") - 1 : page.indexOf("Marcação do CA: ") - 1);
					ca.setRestrictions(removeNewLine(restrictions));
				}

				if (hasObservation) {
					String observation = page.substring(page.indexOf("Observação: ") + 12,
							page.indexOf(LN + "Marcação do CA:"));
					ca.setObservation(removeNewLine(observation));
				}

				if (hasCaLocation) {
					int indexOfReferences = page.indexOf(LN + "Referências:");
					int indexOfCaLocation = page.indexOf(LN + "Marcação do CA:");
					String caLocation = "";
					if (indexOfReferences > indexOfCaLocation) {
						caLocation = page.substring(indexOfCaLocation + 16, indexOfReferences);
					} else {
						caLocation = page.substring(indexOfCaLocation + 16,
								page.indexOf(LN + "Referências:", indexOfReferences + 1));
					}
					ca.setCaLocation(removeNewLine(caLocation));
				}

				int indexOfSize = page.indexOf(LN + "Tamanhos:");
				int indexOfColors = page.indexOf("Cores: ");
				int indexOfTechnician = page.indexOf(LN + "Responsável Técnico:");
				int indexOfProfessionalRegistration = page.indexOf("Nº Registro Profissional:");

				if (hasReferences) {
					int nReferences = StringUtils.countMatches(page, LN + "Referências:");
					String references = "";
					int referenceIndex = page.indexOf(LN + "Referências:");
					int approvedForIndex = page.indexOf(LN + "Aprovado para:");
					if (nReferences > 1 && referenceIndex < approvedForIndex) {
						referenceIndex = page.indexOf(LN + "Referências:", referenceIndex + 1);
					}
					boolean done = false;
					if (indexOfColors < indexOfSize) {
						for (int k = 0; k < line.length; k++) {
							if (line[k].contains("Referências:")) {
								references = line[k].substring(12, line[k].length());
								if (references == null || references.isEmpty()) {
									if (!line[k + 1].contains("Cores:") || !line[k + 1].contains("Tamanhos:")
											|| !line[k + 1].contains("Nº Registro Profissional:")
											|| !line[k + 1].contains("Responsável Técnico:")
											|| !line[k + 1].contains("Marcação do selo do Inmetro::")
											|| !line[k + 1].contains("Normas técnicas:")
											|| !line[k + 1].contains("Laudos:") || !line[k + 1].contains("Empresa:")) {
										references = line[k + 1].substring(0, line[k + 1].length());
									}
								}
								break;
							}
						}
					} else if (hasSize && !done) {
						references = page.substring(referenceIndex + 13, page.indexOf(LN + "Tamanhos:"));
						done = true;
					} else if (hasColors && !done) {
						references = page.substring(referenceIndex + 13, page.indexOf("Cores: ") - 1);
						done = true;
					} else if (hasTechnician && !done) {
						if (indexOfTechnician > indexOfProfessionalRegistration) {
							references = page.substring(referenceIndex + 13, page.indexOf("Nº Registro Profissional:"));
						} else {
							references = page.substring(referenceIndex + 13, page.indexOf(LN + "Responsável Técnico:"));
						}
						done = true;
					} else if (hasInmetroSticker && !done) {
						references = page.substring(referenceIndex + 13,
								page.indexOf(LN + "Marcação do selo do Inmetro:"));
						done = true;
					} else if (hasTechRules && !done) {
						references = page.substring(referenceIndex + 13, page.indexOf(LN + "Normas técnicas:"));
						done = true;
					} else if (hasReports && !done) {
						references = page.substring(referenceIndex + 13, page.indexOf(LN + "Laudos:"));
					} else {
						references = page.substring(referenceIndex + 13, page.indexOf(LN + "Empresa:"));
					}

					ca.setReferences(removeNewLine(references));
				}

				if (hasSize) {
					String size = "";
					boolean done = false;
					if (hasColors && !done) {
						if (indexOfColors > indexOfSize) {
							size = page.substring(indexOfSize + 11, indexOfColors - 1);
						} else {
							for (String string : line) {
								if (string.contains("Tamanhos: ")) {
									size = string.substring(10, string.length());
									break;
								}
							}
						}
						done = true;
					} else if (hasTechnician && !done) {
						size = page.substring(page.indexOf(LN + "Tamanhos:") + 11,
								page.indexOf("Responsável Técnico:") - 1);
						done = true;
					} else if (hasInmetroSticker && !done) {
						size = page.substring(page.indexOf(LN + "Tamanhos:") + 11,
								page.indexOf("Marcação do selo do Inmetro:") - 1);
						done = true;
					} else if (hasTechRules && !done) {
						size = page.substring(page.indexOf(LN + "Tamanhos:") + 11,
								page.indexOf("Normas técnicas: ") - 1);
					} else if (hasReports && !done) {
						size = page.substring(page.indexOf(LN + "Tamanhos:") + 11, page.indexOf(LN + "Laudos:") - 1);
						done = true;
					} else {
						size = page.substring(page.indexOf(LN + "Tamanhos:") + 11, page.indexOf(LN + "Empresa: ") - 1);
					}
					ca.setSize(removeNewLine(size));
				}

				if (hasColors) {
					String colors = "";
					boolean done = false;
					if (hasTechnician && !done) {
						colors = page.substring(page.indexOf("Cores: ") + 7, page.indexOf("Responsável Técnico:") - 1);
						done = true;
					} else if (hasInmetroSticker && !done) {
						colors = page.substring(page.indexOf("Cores: ") + 7,
								page.indexOf("Marcação do selo do Inmetro:") - 1);
						done = true;
					} else if (indexOfColors < indexOfSize) {
						for (String string : line) {
							if (string.contains("Cores: ")) {
								colors = string.substring(7, string.length());
								break;
							}
						}
					} else if (hasTechRules) {
						colors = page.substring(page.indexOf("Cores: ") + 7, page.indexOf(LN + "Normas técnicas: "));
					} else if (hasReports) {
						colors = page.substring(page.indexOf("Cores: ") + 7, page.indexOf(LN + "Laudos:"));
					}
					ca.setColors(removeNewLine(colors));
				}

				if (hasTechnician) {
					String technician = "";
					if (indexOfTechnician > indexOfProfessionalRegistration) {
						technician = page.substring(page.indexOf("Nº Registro Profissional: ") + 26,
								page.indexOf("Empresa:") - 1);
						technician = technician.substring(technician.indexOf("Responsável Técnico:") + 20,
								technician.indexOf(LN, technician.indexOf(LN) + 1));
					} else {
						technician = page.substring(indexOfTechnician + 21, indexOfProfessionalRegistration - 1);
					}
					ca.setTechnician(removeNewLine(technician));
				}

				if (hasProfessionalRegistration) {
					String professionalRegistration = "";
					if (indexOfTechnician > indexOfProfessionalRegistration) {
						professionalRegistration = page.substring(page.indexOf("Nº Registro Profissional: ") + 26,
								page.indexOf("Empresa:") - 1);
						String[] split = professionalRegistration.split(LN);
						professionalRegistration = split[0] + " " + split[2];
					} else {
						professionalRegistration = page.substring(page.indexOf("Nº Registro Profissional: ") + 26,
								page.indexOf("Empresa:") - 1);
					}
					ca.setProfessionalRegistration(removeNewLine(professionalRegistration));
				}

				if (hasInmetroSticker) {
					String inmetroSticker = page.substring(page.indexOf("Marcação do selo do Inmetro: ") + 29,
							page.indexOf("Atestado de conformidade Inmetro:") - 1);
					ca.setInmetroSticker(removeNewLine(inmetroSticker));
				}

				if (hasInmetroConformityProof) {
					String inmetroConformityProof = "";
					boolean done = false;
					if (hasTechRules && !done) {
						inmetroConformityProof = page.substring(page.indexOf("Atestado de conformidade Inmetro: ") + 34,
								page.indexOf("Normas técnicas: ") - 1);
						done = true;
					}
					if (hasReports && !done) {
						inmetroConformityProof = page.substring(page.indexOf("Atestado de conformidade Inmetro: ") + 34,
								page.indexOf(LN + "Laudos:"));
						done = true;
					}
					if (hasCompany && !done) {
						inmetroConformityProof = page.substring(page.indexOf("Atestado de conformidade Inmetro: ") + 34,
								page.indexOf(LN + "Empresa:"));
						done = true;
					}

					ca.setInmetroConformityProof(removeNewLine(inmetroConformityProof));
				}

				if (hasTechRules) {
					String techRules = page.substring(page.indexOf("Normas técnicas: ") + 17,
							page.indexOf("Laudos:") == -1 ? page.indexOf("Empresa: ") - 1 : page.indexOf("Laudos:"));
					String[] techRulesArray = removeNewLine(techRules).split(";");
					List<String> technicalRules = new ArrayList<String>(Arrays.asList(techRulesArray));
					ca.setTechnicalRules(technicalRules);
				}

				if (hasReports) {
					List<Report> reports = new ArrayList<Report>();
					int nReports = StringUtils.countMatches(page, "Nº. Laudo: ");
					for (int j = 1; j <= nReports; j++) {
						Report report = new Report();
						int numberIndex = StringUtils.ordinalIndexOf(page, "Nº. Laudo: ", j);
						int labIndex = StringUtils.ordinalIndexOf(page, "Laboratório: ", j);

						String reportNumber = page.substring(numberIndex + 11, labIndex - 1);
						String laboratoryName = "";

						if (j + 1 <= nReports) {
							laboratoryName = page.substring(labIndex + 13,
									StringUtils.ordinalIndexOf(page, "Nº. Laudo: ", j + 1) - 1);
						}
						if (j == nReports) {
							laboratoryName = page.substring(labIndex + 13, page.indexOf("Empresa: ") - 1);
						}

						report.setLaboratoryName(removeNewLine(laboratoryName));
						report.setReportNumber(removeNewLine(reportNumber));
						reports.add(report);
					}
					ca.setReports(reports);
				}

			}
		} catch (Exception e) {
			if (e instanceof InvalidPdfException)
				throw e;
			// e.printStackTrace();
			Logger.trace(e, "CA file " + pathToPDF);
		}
		// TODO: put this back when done
		return ca;
		// System.out.println(ca);
	}

	private static String removeNewLine(String string) {
		return string.replace(LN, " ").replace("\r", " ").trim();
	}
}

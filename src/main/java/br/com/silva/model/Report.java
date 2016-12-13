package br.com.silva.model;

import java.io.Serializable;

public class Report implements Serializable {

	private static final long serialVersionUID = -6037648624582682454L;

	private String laboratoryCNPJ;
	private String laboratoryName;
	private String reportNumber;

	/**
	 * Empty Constructor
	 */
	public Report() {
	}

	/**
	 * @param laboratoryCNPJ
	 * @param laboratoryName
	 * @param reportNumber
	 */
	public Report(String laboratoryCNPJ, String laboratoryName, String reportNumber) {
		this.laboratoryCNPJ = laboratoryCNPJ;
		this.laboratoryName = laboratoryName;
		this.reportNumber = reportNumber;
	}

	/**
	 * @return the laboratoryCNPJ
	 */
	public String getLaboratoryCNPJ() {
		return laboratoryCNPJ;
	}

	/**
	 * @param laboratoryCNPJ
	 *            the laboratoryCNPJ to set
	 */
	public void setLaboratoryCNPJ(String laboratoryCNPJ) {
		this.laboratoryCNPJ = laboratoryCNPJ;
	}

	/**
	 * @return the laboratoryName
	 */
	public String getLaboratoryName() {
		return laboratoryName;
	}

	/**
	 * @param laboratoryName
	 *            the laboratoryName to set
	 */
	public void setLaboratoryName(String laboratoryName) {
		this.laboratoryName = laboratoryName;
	}

	/**
	 * @return the reportNumber
	 */
	public String getReportNumber() {
		return reportNumber;
	}

	/**
	 * @param reportNumber
	 *            the reportNumber to set
	 */
	public void setReportNumber(String reportNumber) {
		this.reportNumber = reportNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Report [laboratoryCNPJ=" + laboratoryCNPJ + ", laboratoryName=" + laboratoryName + ", reportNumber="
				+ reportNumber + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reportNumber == null) ? 0 : reportNumber.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Report other = (Report) obj;
		if (reportNumber == null) {
			if (other.reportNumber != null)
				return false;
		} else if (!reportNumber.equals(other.reportNumber))
			return false;
		return true;
	}

}

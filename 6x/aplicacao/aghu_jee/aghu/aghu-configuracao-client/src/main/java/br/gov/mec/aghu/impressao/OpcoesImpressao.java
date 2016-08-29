package br.gov.mec.aghu.impressao;

import org.apache.commons.lang3.ArrayUtils;

public class OpcoesImpressao {

	private static final String[] NO_MARGINS_OPTIONS = { "-o", "page-left=0",
			"-o", "page-right=0", "-o", "page-top=0", "-o", "page-bottom=0" };
	private static final String[] FIT_TO_PAGE_OPTIONS = { "-o", "fit-to-page" };

	private boolean noMargins;
	private boolean fitToPage;

	public OpcoesImpressao() {

	}

	public OpcoesImpressao(boolean noMargins, boolean fitToPage) {
		this.noMargins = noMargins;
		this.fitToPage = fitToPage;
	}

	/**
	 * Indica para que a impressão reduza as margens a zero.<br />
	 * Usado para evitar que a impressão seja redimensionada para caber na
	 * página causando, por exemplo, deslocamento na impressão de etiquetas.
	 * 
	 * @return
	 */
	public boolean isNoMargins() {
		return noMargins;
	}

	public void setNoMargins(boolean noMargins) {
		this.noMargins = noMargins;
	}

	/**
	 * Indica para que a impressão seja redimensionado para caber na página.<br />
	 * Usado quando o documento possui margens muito pequenas, evitando que o
	 * documento seja truncado nas margens.
	 * 
	 * @return
	 */
	public boolean isFitToPage() {
		return fitToPage;
	}

	public void setFitToPage(boolean fitToPage) {
		this.fitToPage = fitToPage;
	}

	@Override
	public String toString() {
		return ArrayUtils.toString(this.toArray());
	}

	public String[] toArray() {
		String[] result = {};
		if (this.noMargins) {
			result = (String[]) ArrayUtils.addAll(result, NO_MARGINS_OPTIONS);
		}
		if (this.fitToPage) {
			result = (String[]) ArrayUtils.addAll(result, FIT_TO_PAGE_OPTIONS);
		}

		return result;
	}

}

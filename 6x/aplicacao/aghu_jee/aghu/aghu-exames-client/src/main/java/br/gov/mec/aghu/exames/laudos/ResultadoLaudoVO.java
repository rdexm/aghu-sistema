package br.gov.mec.aghu.exames.laudos;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Laudo gerado pelo Dynamics Report.
 * 
 * Compoe um output stream e as fontes alteradas 
 * 
 * @author twickert
 * 
 */
public class ResultadoLaudoVO implements Serializable {

	private static final long serialVersionUID = 5673245295764619481L;

	private ByteArrayOutputStream outputStreamLaudo;
	
	private List<String> fontsAlteradas;

	public ByteArrayOutputStream getOutputStreamLaudo() {
		return outputStreamLaudo;
	}

	public void setOutputStreamLaudo(ByteArrayOutputStream outputStreamLaudo) {
		this.outputStreamLaudo = outputStreamLaudo;
	}

	public List<String> getFontsAlteradas() {
		return fontsAlteradas;
	}

	public void setFontsAlteradas(List<String> fontsAlteradas) {
		this.fontsAlteradas = fontsAlteradas;
	}
	
	

}

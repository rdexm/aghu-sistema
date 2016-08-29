package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;

public class ExameSuggestionVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7824223859519841689L;

	private boolean selecionado = false;

	private String exameSigla;
	private String sinonimoExameNome;
	private String exaDescricao;
	private String manDescricao;
	private Integer manSeq;
	private Short ufeUnfSeq;
	private String ufeUnfDescricao;
	private String sinonimos;
	private String sinonimosComAcento;

	public ExameSuggestionVO() {

	}

	public ExameSuggestionVO(String exameSigla, String sinonimoExameNome, String exaDescricao, String manDescricao,
			Integer manSeq, Short ufeUnfSeq, String ufeUnfDescricao) {
		super();
		this.exameSigla = exameSigla;
		this.sinonimoExameNome = sinonimoExameNome;
		this.exaDescricao = exaDescricao;
		this.manDescricao = manDescricao;
		this.manSeq = manSeq;
		this.ufeUnfSeq = ufeUnfSeq;
		this.ufeUnfDescricao = ufeUnfDescricao;
	}

	/**
	 * @param exameSigla
	 *            the exameSigla to set
	 */
	public void setExameSigla(String exameSigla) {
		this.exameSigla = exameSigla;
	}

	/**
	 * @return the exameSigla
	 */
	public String getExameSigla() {
		return exameSigla;
	}

	/**
	 * @param sinonimoExameNome
	 *            the sinonimoExameNome to set
	 */
	public void setSinonimoExameNome(String sinonimoExameNome) {
		this.sinonimoExameNome = sinonimoExameNome;
	}

	/**
	 * @return the sinonimoExameNome
	 */
	public String getSinonimoExameNome() {
		return sinonimoExameNome;
	}

	private String separador = " - ";

	/**
	 * Retorna o id formatado da seguinte forma: SIGLA EXAME - SEQ MATERIAL -
	 * SEQ UNID EXEC
	 * 
	 * @return
	 */
	public String getIdExameFormatado() {
		StringBuilder idExameFormatada = new StringBuilder();

		idExameFormatada.append(exameSigla).append(separador).append(getManSeq()).append(separador)
				.append(getUfeUnfSeq());

		return idExameFormatada.toString();
	}

	/**
	 * Retorna a descricao formatada da seguinte forma: SINONIMO EXAME -
	 * DESCRICAO MATERIAL - DESCRICAO UNID EXEC
	 * 
	 * @return
	 */
	public String getDescricaoExameFormatada() {
		String separador = " - ";
		return getUfeUnfDescricao() + separador + getManDescricao() + separador + getSinonimoExameNome();
	}

	/**
	 * Retorna a descricao formatada da seguinte forma: DESCRIÇÃO USUAL EXAME -
	 * DESCRICAO MATERIAL - DESCRICAO UNID EXEC
	 * 
	 * @return
	 */
	public String getDescricaoExameUsualFormatada() {
		return getDescricaoExameFormatada(getExaDescricao(), getManDescricao(), getUfeUnfDescricao());
	}

	public String getDescricaoExameFormatada(String nomeExame, String materialAnalise, String unidadeFuncional) {
		StringBuilder descricaoExameExameFormatada = new StringBuilder();
		String separador = " - ";

		descricaoExameExameFormatada.append(nomeExame).append(separador).append(materialAnalise).append(separador)
				.append(unidadeFuncional);

		return descricaoExameExameFormatada.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ExameSuggestionVO)) {
			return false;
		}
		ExameSuggestionVO other = (ExameSuggestionVO) obj;
		if (other.getIdExameFormatado() == null || this.getIdExameFormatado() == null) {
			return false;
		}

		return (other.getIdExameFormatado().equalsIgnoreCase(this.getIdExameFormatado()));
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getExaDescricao() {
		return exaDescricao;
	}

	public void setExaDescricao(String exaDescricao) {
		this.exaDescricao = exaDescricao;
	}

	public String getManDescricao() {
		return manDescricao;
	}

	public void setManDescricao(String manDescricao) {
		this.manDescricao = manDescricao;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public String getUfeUnfDescricao() {
		return ufeUnfDescricao;
	}

	public void setUfeUnfDescricao(String ufeUnfDescricao) {
		this.ufeUnfDescricao = ufeUnfDescricao;
	}

	public String getSeparador() {
		return separador;
	}

	public void setSeparador(String separador) {
		this.separador = separador;
	}

	public String getSinonimos() {
		String retorno = sinonimos != null ? sinonimos.split("@@@")[1].replace("***", "\r\n"): null;
		return retorno;
	}

	public void setSinonimos(String sinonimos) {
		this.sinonimos = sinonimos;
	}

	public String getSinonimosNaoFormatado() {
		return sinonimos;
	}

	public String getSinonimosComAcento() {
		
		String retorno = sinonimosComAcento != null ? sinonimosComAcento.split("@@@")[1].replace("***", "\r\n"):null;
		return retorno;
	}

	public void setSinonimosComAcento(String sinonimosComAcento) {
		this.sinonimosComAcento = sinonimosComAcento;
	}
	
	
}

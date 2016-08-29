package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelUnfExecutaExames;


public class UnfExecutaSinonimoExameVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7824223859519841689L;
	private AelUnfExecutaExames unfExecutaExame;
	private AelSinonimoExame sinonimoExame;
	
	private String exameSigla;
	private String sinonimoExameNome;
	private String materialAnaliseUnidadeFuncionalDescricao;
	private boolean selecionado = false;
	private AelSitItemSolicitacoes situacao;
	private Boolean checkUrgente = Boolean.FALSE;
	private Date dataProgr;
	private Boolean editado = Boolean.FALSE;
	private Boolean calendar = Boolean.TRUE;
	
	//#2253
	private Integer numeroAmostra;
	private Date intervaloHoras;
	private Integer intervaloDias;
	
	public UnfExecutaSinonimoExameVO() {
		
	}
	
	public UnfExecutaSinonimoExameVO(AelUnfExecutaExames unfExecEx, AelSinonimoExame sinonimo) {
		newInstance(unfExecEx, sinonimo);
	}
	
	public UnfExecutaSinonimoExameVO(AelUnfExecutaExames unfExecutaExame) {
		if (unfExecutaExame == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		newInstance(unfExecutaExame, new AelSinonimoExame());
	}
	
	private void newInstance(AelUnfExecutaExames unfExecEx, AelSinonimoExame sinonimo) {
		if (unfExecEx == null || sinonimo == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		// Correcao temporaria para layinitialization.
//		unfExecEx.getAelExamesMaterialAnalise();
//		unfExecEx.getAelExamesMaterialAnalise().getAelExames();
//		unfExecEx.getAelExamesMaterialAnalise().getAelMateriaisAnalises();
//		BeanUtils.copyProperties(dest, orig);
		
		this.unfExecutaExame = unfExecEx;
		
		this.setExameSigla(this.unfExecutaExame.getId().getEmaExaSigla());
		this.setSinonimoExameNome(sinonimo.getNome());
		
		StringBuffer manDesc = new StringBuffer();
		if (this.unfExecutaExame.getAelExamesMaterialAnalise() != null) {
			if (this.unfExecutaExame.getAelExamesMaterialAnalise().getAelMateriaisAnalises() != null) {
				manDesc.append(this.unfExecutaExame.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao());
			}
			if (unfExecEx.getAelExamesMaterialAnalise().getAelExames() != null) {
				// Correcao temporaria para layinitialization.
				unfExecEx.getAelExamesMaterialAnalise().getAelExames().getDescricaoUsual();				
			}
		}
		
		if (this.unfExecutaExame.getUnidadeFuncional() != null 
				&& StringUtils.isNotBlank(this.unfExecutaExame.getUnidadeFuncional().getDescricao())) {
			manDesc.append(" (").append(this.unfExecutaExame.getUnidadeFuncional().getDescricao()).append(')'); 
		}
		this.setMaterialAnaliseUnidadeFuncionalDescricao(manDesc.length() > 0 ? manDesc.toString() : null);
	}

	/**
	 * @param exameSigla the exameSigla to set
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
	 * @param sinonimoExameNome the sinonimoExameNome to set
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
	 * Retorna o id formatado da seguinte forma: SIGLA EXAME - SEQ MATERIAL - SEQ UNID EXEC
	 * @return
	 */
	public String getIdExameFormatado() {
		StringBuilder idExameFormatada = new StringBuilder();
		
		idExameFormatada.append(exameSigla).append(separador).
									append(getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq()).
									append(separador).append(getUnfExecutaExame().getUnidadeFuncional().getSeq());
		
		return  idExameFormatada.toString();
	}
	
	/**
	 * Retorna a descricao formatada da seguinte forma: SINONIMO EXAME - DESCRICAO MATERIAL - DESCRICAO UNID EXEC
	 * @return
	 */
	public String getDescricaoExameFormatada() {
		return getDescricaoExameFormatada(
				getSinonimoExameNome(), 
				getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao(), 
				getUnfExecutaExame().getUnidadeFuncional().getDescricao());
	}
	
	/**
	 * Retorna a descricao formatada da seguinte forma: DESCRIÇÃO USUAL EXAME - DESCRICAO MATERIAL - DESCRICAO UNID EXEC
	 * @return
	 */
	public String getDescricaoExameUsualFormatada() {
		return getDescricaoExameFormatada(
				getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames().getDescricaoUsual(), 
				getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao(), 
				getUnfExecutaExame().getUnidadeFuncional().getDescricao());
	}
	
	public String getDescricaoUsualExame(){
		return getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames().getDescricaoUsual();
	}
	
	public String getDescricaoExameFormatada(String nomeExame, String materialAnalise, String unidadeFuncional) {
		StringBuilder descricaoExameExameFormatada = new StringBuilder();
		String separador = " - ";
		
		descricaoExameExameFormatada.
		append(nomeExame).
		append(separador).
		append(materialAnalise).
		append(separador).
		append(unidadeFuncional);
		
		return  descricaoExameExameFormatada.toString();
	}
	
	
	/**
	 * @param materialAnaliseUnidadeFuncionalDescricao the materialAnaliseUnidadeFuncionalDescricao to set
	 */
	public void setMaterialAnaliseUnidadeFuncionalDescricao(
			String materialAnaliseUnidadeFuncionalDescricao) {
		this.materialAnaliseUnidadeFuncionalDescricao = materialAnaliseUnidadeFuncionalDescricao;
	}
	/**
	 * @return the materialAnaliseUnidadeFuncionalDescricao
	 */
	public String getMaterialAnaliseUnidadeFuncionalDescricao() {
		return materialAnaliseUnidadeFuncionalDescricao;
	}


	public AelUnfExecutaExames getUnfExecutaExame() {
		return unfExecutaExame;
	}


	public void setUnfExecutaExame(AelUnfExecutaExames unfExecutaExame) {
		this.unfExecutaExame = unfExecutaExame;
	}


	public AelSinonimoExame getSinonimoExame() {
		return sinonimoExame;
	}


	public void setSinonimoExame(AelSinonimoExame sinonimoExame) {
		this.sinonimoExame = sinonimoExame;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UnfExecutaSinonimoExameVO)) {
			return false;
		}
		UnfExecutaSinonimoExameVO other = (UnfExecutaSinonimoExameVO) obj;
		if (other.getSinonimoExame() == null || other.getSinonimoExame().getId() == null
				|| other.getUnfExecutaExame() == null || other.getUnfExecutaExame().getId() == null
				|| this.getSinonimoExame() == null || this.getSinonimoExame().getId() == null
				|| this.getUnfExecutaExame() == null || this.getUnfExecutaExame().getId() == null) {
			return false;
		}
		
		return (other.getSinonimoExame().getId().equals(this.getSinonimoExame().getId()) 
				&& other.getUnfExecutaExame().getId().equals(this.getUnfExecutaExame().getId()));
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public Boolean getCheckUrgente() {
		return checkUrgente;
	}

	public void setCheckUrgente(Boolean checkUrgente) {
		this.checkUrgente = checkUrgente;
	}

	public Date getDataProgr() {
		return dataProgr;
	}

	public void setDataProgr(Date dataProgr) {
		this.dataProgr = dataProgr;
	}

	public Boolean isEditado() {
		return editado;
	}

	public void setEditado(Boolean editado) {
		this.editado = editado;
	}

	public Boolean getCalendar() {
		return calendar;
	}

	public void setCalendar(Boolean calendar) {
		this.calendar = calendar;
	}

	public Integer getNumeroAmostra() {
		return numeroAmostra;
	}

	public void setNumeroAmostra(Integer numeroAmostra) {
		this.numeroAmostra = numeroAmostra;
	}

	public Date getIntervaloHoras() {
		return intervaloHoras;
	}

	public void setIntervaloHoras(Date intervaloHoras) {
		this.intervaloHoras = intervaloHoras;
	}

	public Integer getIntervaloDias() {
		return intervaloDias;
	}

	public void setIntervaloDias(Integer intervaloDias) {
		this.intervaloDias = intervaloDias;
	}
	
}

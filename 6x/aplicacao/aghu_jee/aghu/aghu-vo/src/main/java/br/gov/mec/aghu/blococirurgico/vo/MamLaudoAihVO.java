package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.dominio.DominioIndSituacaoLaudoAih;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.core.commons.BaseBean;

public class MamLaudoAihVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3416769889272204187L;
	private Long seq;
	private Date dthrCriacao;
	private DominioIndPendenteLaudoAih indPendente;
	private MamLaudoAih mamLaudoAihs; // lai_Seq
	private Date dtProvavelInternacao;
	private AghEspecialidades especialidade;
	private DominioIndSituacaoLaudoAih indSituacao;
	private String obsRevisaoMedica;
	private AghEquipes equipe;

	public Long getSeq() {
		return seq;
	}

	public Date getDthrCriacao() {
		return dthrCriacao;
	}

	public DominioIndPendenteLaudoAih getIndPendente() {
		return indPendente;
	}

	public MamLaudoAih getMamLaudoAihs() {
		return mamLaudoAihs;
	}

	public Date getDtProvavelInternacao() {
		return dtProvavelInternacao;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public DominioIndSituacaoLaudoAih getIndSituacao() {
		return indSituacao;
	}

	public String getObsRevisaoMedica() {
		return obsRevisaoMedica;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	public void setIndPendente(DominioIndPendenteLaudoAih indPendente) {
		this.indPendente = indPendente;
	}

	public void setMamLaudoAihs(MamLaudoAih mamLaudoAihs) {
		this.mamLaudoAihs = mamLaudoAihs;
	}

	public void setDtProvavelInternacao(Date dtProvavelInternacao) {
		this.dtProvavelInternacao = dtProvavelInternacao;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public void setIndSituacao(DominioIndSituacaoLaudoAih indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setObsRevisaoMedica(String obsRevisaoMedica) {
		this.obsRevisaoMedica = obsRevisaoMedica;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public enum Fields {
		SEQ("seq"), 
		DTHR_CRIACAO("dthrCriacao"), 
		ESPECIALIDADE("especialidade"), 
		DT_PROVAVEL_INTERNACAO("dtProvavelInternacao"), 
		IND_PENDENTE("indPendente"), 
		MAM_LAUDO_AIHS("mamLaudoAihs"), 
		IND_SITUACAO("indSituacao"), 
		OBS_REVISAO_MEDICA("obsRevisaoMedica"),
		EQUIPE("equipe");
		private String fields;
		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}

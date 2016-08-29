package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConsulta;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;

public class FiltroEspecialidadeDisponivelVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3744659660855302289L;
	
	private Date dtInicial;
	private Date dtFinal;
	private AghEspecialidadeVO especialidade;
	private AacCondicaoAtendimento condicaoAtendimento;
	private AacPagador pagador;
	private AacTipoAgendamento autorizacao;
	private DominioSituacaoConsulta situacao;
	
	public Date getDtInicial() {
		return dtInicial;
	}
	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}
	public Date getDtFinal() {
		return dtFinal;
	}
	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}
	public AghEspecialidadeVO getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(AghEspecialidadeVO especialidade) {
		this.especialidade = especialidade;
	}
	public AacCondicaoAtendimento getCondicaoAtendimento() {
		return condicaoAtendimento;
	}
	public void setCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) {
		this.condicaoAtendimento = condicaoAtendimento;
	}
	public AacPagador getPagador() {
		return pagador;
	}
	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}
	public AacTipoAgendamento getAutorizacao() {
		return autorizacao;
	}
	public void setAutorizacao(AacTipoAgendamento autorizacao) {
		this.autorizacao = autorizacao;
	}
	public DominioSituacaoConsulta getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoConsulta situacao) {
		this.situacao = situacao;
	}
}

package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;

/**
 *  Os dados armazenados nesse objeto representam a view V_AIP_SOLICITANTES usada
 * na tela de 'Relatório de Movimentação por Situação'.
 * 
 * @author Ricardo Costa
 * @author rcorvalao
 *
 */
public class VAipSolicitantesVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1954618229478828029L;
	
	private Short codigo;
	private String descricao;
	private DominioSimNao separacaoPrevia;
	private DominioTodosUltimo volumesManuseados;
	private DominioSituacao indSituacao;
	private AghUnidadesFuncionais unidadesFuncionais;
	private AghOrigemEventos origemEventos;
	private AipFinalidadesMovimentacao finalidadesMovimentacao;
	private DominioSimNao mensagemSamis;

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getSeparacaoPrevia() {
		return separacaoPrevia;
	}

	public void setSeparacaoPrevia(DominioSimNao separacaoPrevia) {
		this.separacaoPrevia = separacaoPrevia;
	}

	public DominioTodosUltimo getVolumesManuseados() {
		return volumesManuseados;
	}

	public void setVolumesManuseados(DominioTodosUltimo volumesManuseados) {
		this.volumesManuseados = volumesManuseados;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	public AghOrigemEventos getOrigemEventos() {
		return origemEventos;
	}

	public void setOrigemEventos(AghOrigemEventos origemEventos) {
		this.origemEventos = origemEventos;
	}

	public AipFinalidadesMovimentacao getFinalidadesMovimentacao() {
		return finalidadesMovimentacao;
	}

	public void setFinalidadesMovimentacao(
			AipFinalidadesMovimentacao finalidadesMovimentacao) {
		this.finalidadesMovimentacao = finalidadesMovimentacao;
	}

	public DominioSimNao getMensagemSamis() {
		return mensagemSamis;
	}

	public void setMensagemSamis(DominioSimNao mensagemSamis) {
		this.mensagemSamis = mensagemSamis;
	}

}

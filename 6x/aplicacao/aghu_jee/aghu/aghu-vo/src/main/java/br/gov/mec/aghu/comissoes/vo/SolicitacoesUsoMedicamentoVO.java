package br.gov.mec.aghu.comissoes.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAghUnidFuncional;
import br.gov.mec.aghu.model.VMedicoSolicitante;
import br.gov.mec.aghu.model.VMpmpProfInterna;


public class SolicitacoesUsoMedicamentoVO {
	
	private Integer codProntuario;
	
	private AfaMedicamento medicamento;
	
	private DominioIndRespAvaliacao avaliador;

	private DominioSituacaoSolicitacaoMedicamento situacao;
	
	private AfaTipoUsoMdto tipoUso;
	
	private AfaGrupoUsoMedicamento grupoUsoMedicamento;
	
	private VAghUnidFuncional vAghUnidFuncional;
	
	private RapServidores servidorValida;
	
	private RapServidores servidorProfessor;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private DominioSimNao indAntimicrobianos;
	
	private VMpmpProfInterna vMpmpProfInterna;
	
	private VMedicoSolicitante vMedicoSolicitante;
	
	public enum Fields {
		COD_PRONTUARIO("codProntuario"),
		MEDICAMENTO("medicamento"),
		AVALIADOR("avaliador"),
		SITUACAO("situacao"),
		TIPO_USO("tipoUso"),
		GRUPO_USO("grupoUsoMedicamento"),
		SERVIDOR_VALIDA("servidorValida"),
		SERVIDOR_PROFESSOR("servidorProfessor"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		V_MPM_PROF_INTERNA("vMpmpProfInterna"),
		V_MEDICO_SOLICITANTE("vMedicoSolicitante"),
		IND_ANTIMICROBIANOS("indAntimicrobianos");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getCodProntuario() {
		return codProntuario;
	}

	public void setCodProntuario(Integer codProntuario) {
		this.codProntuario = codProntuario;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}
	
	public DominioIndRespAvaliacao getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(DominioIndRespAvaliacao avaliador) {
		this.avaliador = avaliador;
	}

	public DominioSituacaoSolicitacaoMedicamento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoSolicitacaoMedicamento situacao) {
		this.situacao = situacao;
	}

	public AfaTipoUsoMdto getTipoUso() {
		return tipoUso;
	}

	public void setTipoUso(AfaTipoUsoMdto tipoUso) {
		this.tipoUso = tipoUso;
	}

	public AfaGrupoUsoMedicamento getGrupoUsoMedicamento() {
		return grupoUsoMedicamento;
	}

	public void setGrupoUsoMedicamento(AfaGrupoUsoMedicamento grupoUsoMedicamento) {
		this.grupoUsoMedicamento = grupoUsoMedicamento;
	}

	public VAghUnidFuncional getvAghUnidFuncional() {
		return vAghUnidFuncional;
	}

	public void setvAghUnidFuncional(VAghUnidFuncional vAghUnidFuncional) {
		this.vAghUnidFuncional = vAghUnidFuncional;
	}

	public DominioSimNao getIndAntimicrobianos() {
		return indAntimicrobianos;
	}

	public void setIndAntimicrobianos(DominioSimNao indAntimicrobianos) {
		this.indAntimicrobianos = indAntimicrobianos;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public RapServidores getServidorValida() {
		return servidorValida;
	}

	public void setServidorValida(RapServidores servidorValida) {
		this.servidorValida = servidorValida;
	}

	public RapServidores getServidorProfessor() {
		return servidorProfessor;
	}

	public void setServidorProfessor(RapServidores servidorProfessor) {
		this.servidorProfessor = servidorProfessor;
	}

	public VMpmpProfInterna getvMpmpProfInterna() {
		return vMpmpProfInterna;
	}

	public void setvMpmpProfInterna(VMpmpProfInterna vMpmpProfInterna) {
		this.vMpmpProfInterna = vMpmpProfInterna;
	}

	public VMedicoSolicitante getvMedicoSolicitante() {
		return vMedicoSolicitante;
	}

	public void setvMedicoSolicitante(VMedicoSolicitante vMedicoSolicitante) {
		this.vMedicoSolicitante = vMedicoSolicitante;
	}

}

package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.commons.BaseBean;

 

public class PlanejamentoCirurgicoVO  implements BaseBean {

	private static final long serialVersionUID = -4655390890419077248L;
	
	private Integer agdSeq;  
	private String dtAgenda;  
	private String tempoSala;
	private String equipe;
	private String crmEquipe;
	private String especialidade;
	private String dtRegistro;
	private String respAgd;
	private String comentario;
	private Integer pacCodigo;
	private String nomePaciente;
	private String idadePac;
	private String sexoPac;
	private String prontuario;
	private String procedimento;
	private String descHemo;
	private Integer qtdeHemo;
	private Integer qtdeHemoAdic;
	private String local;
	private String regime;
	private String ladoCirugia;
	private String materialEspecial;
	private String vSysdate;
	private String unidadeFuncional; //3
	private String diagnostico;
	private List<LinhaReportVO> hemoterapicosList;
	
	//Atributos necessários para as functions que serão processadas na ON
	private Short pucSerVinCodigo;
	private Integer pucSerMatricula;
	private Date alteradoEmMbcAgenda;
	private Date dtHrInclusao;
	private Short serVinCodigo;
	private Integer serMatricula;
	private Short serVinCodigoAlteradoPor;
	private Integer serMatriculaAlteradoPor;
	private String ltoLtoId;
	private Short qrtNumero;
	private Short unfSeq;
	private String cidCodigo;
	private String cidDescricao;
	private Integer prontuarioInteger;
	
	//Atributos que precisam ser feitos CAST antes de ser enviados para o relatório
	private Date dtAgendaDate;
	private Date tempoSalaDate;
	private DominioSexo sexoPacDominio;
	private Short qtdeHemoShort;
	private Short qtdeHemoAdicShort;
	private DominioLadoCirurgiaAgendas ladoCirugiaDominio;
	private DominioRegimeProcedimentoCirurgicoSus regimeDominio;

	public enum Fields {
		

		AGD_SEQ("agdSeq"),  
		DT_AGENDA("dtAgenda"),  
		TEMPO_SALA("tempoSala"),
		EQUIPE("equipe"),
		CRM_EQUIPE("crmEquipe"),
		ESPECIALIDADE("especialidade"),
		DT_REGISTRO("dtRegistro"),
		RESP_AGD("respAgd"),
		COMENTARIO("comentario"),
		PAC_CODIGO("pacCodigo"),
		NOME_PACIENTE("nomePaciente"),
		IDADE_PAC("idadePac"),
		SEXO_PAC("sexoPac"),
		PRONTUARIO("prontuario"),
		PROCEDIMENTO("procedimento"),
		DESC_HEMO("descHemo"),
		QTDE_HEMO("qtdeHemo"),
		QTDE_HEMO_ADIC("qtdeHemoAdic"),
		LOCAL("local"),
		REGIME("regime"),
		LADO_CIRURGIA("ladoCirugia"),
		MATERIAL_ESPECIAL("materialEspecial"),
		SYSDATE("vSysdate"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		DIAGNOSTICO("diagnostico"),
		
		PUC_SER_VIN_CODIGO("pucSerVinCodigo"),
		PUC_SER_MATRICULA("pucSerMatricula"),
		ALTERADO_EM_MBC_AGENDA("alteradoEmMbcAgenda"),
		DTHR_INCLUSAO("dtHrInclusao"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO_ALTERADO_POR("serVinCodigoAlteradoPor"),
		SER_MATRICULA_ALTERADO_POR("serMatriculaAlteradoPor"),
		LTO_LTO_ID("ltoLtoId"),
		QRT_NUMERO("qrtNumero"),
		UNF_SEQ("unfSeq"),
		CID_CODIGO("cidCodigo"),
		CID_DESCRICAO("cidDescricao"),
		PRONTUARIO_INTEGER("prontuarioInteger"),
		
		DT_AGENDA_DATE("dtAgendaDate"),
		TEMPO_SALA_DATE("tempoSalaDate"),
		SEXO_PAC_DOMINIO("sexoPacDominio"),
		LADO_CIRURGIA_DOMINIO("ladoCirugiaDominio"),
		QTDE_HEMO_SHORT("qtdeHemoShort"),
		QTDE_HEMO_ADIC_SHORT("qtdeHemoAdicShort"),
		REGIME_DOMINIO("regimeDominio")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public PlanejamentoCirurgicoVO(){}


	public Integer getAgdSeq() {
		return agdSeq;
	}


	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}


	public String getDtAgenda() {
		return dtAgenda;
	}


	public void setDtAgenda(String dtAgenda) {
		this.dtAgenda = dtAgenda;
	}


	public String getTempoSala() {
		return tempoSala;
	}


	public void setTempoSala(String tempoSala) {
		this.tempoSala = tempoSala;
	}


	public String getEquipe() {
		return equipe;
	}


	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}


	public String getCrmEquipe() {
		return crmEquipe;
	}


	public void setCrmEquipe(String crmEquipe) {
		this.crmEquipe = crmEquipe;
	}


	public String getEspecialidade() {
		return especialidade;
	}


	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}


	public String getDtRegistro() {
		return dtRegistro;
	}


	public void setDtRegistro(String dtRegistro) {
		this.dtRegistro = dtRegistro;
	}


	public String getRespAgd() {
		return respAgd;
	}


	public void setRespAgd(String respAgd) {
		this.respAgd = respAgd;
	}


	public String getComentario() {
		return comentario;
	}


	public void setComentario(String comentario) {
		this.comentario = comentario;
	}


	public Integer getPacCodigo() {
		return pacCodigo;
	}


	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public String getNomePaciente() {
		return nomePaciente;
	}


	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}


	public String getIdadePac() {
		return idadePac;
	}


	public void setIdadePac(String idadePac) {
		this.idadePac = idadePac;
	}


	public String getSexoPac() {
		return sexoPac;
	}


	public void setSexoPac(String sexoPac) {
		this.sexoPac = sexoPac;
	}


	public String getProntuario() {
		return prontuario;
	}


	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}


	public String getProcedimento() {
		return procedimento;
	}


	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}


	public String getDescHemo() {
		return descHemo;
	}


	public void setDescHemo(String descHemo) {
		this.descHemo = descHemo;
	}


	public Integer getQtdeHemo() {
		return qtdeHemo;
	}


	public void setQtdeHemo(Integer qtdeHemo) {
		this.qtdeHemo = qtdeHemo;
	}


	public Integer getQtdeHemoAdic() {
		return qtdeHemoAdic;
	}


	public void setQtdeHemoAdic(Integer qtdeHemoAdic) {
		this.qtdeHemoAdic = qtdeHemoAdic;
	}


	public String getLocal() {
		return local;
	}


	public void setLocal(String local) {
		this.local = local;
	}


	public String getRegime() {
		return regime;
	}


	public DominioRegimeProcedimentoCirurgicoSus getRegimeDominio() {
		return regimeDominio;
	}


	public void setRegimeDominio(DominioRegimeProcedimentoCirurgicoSus regimeDominio) {
		this.regimeDominio = regimeDominio;
	}


	public void setRegime(String regime) {
		this.regime = regime;
	}


	public String getLadoCirugia() {
		return ladoCirugia;
	}


	public void setLadoCirugia(String ladoCirugia) {
		this.ladoCirugia = ladoCirugia;
	}


	public String getMaterialEspecial() {
		return materialEspecial;
	}


	public void setMaterialEspecial(String materialEspecial) {
		this.materialEspecial = materialEspecial;
	}


	public String getvSysdate() {
		return vSysdate;
	}


	public void setvSysdate(String vSysdate) {
		this.vSysdate = vSysdate;
	}


	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}


	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public String getDiagnostico() {
		return diagnostico;
	}


	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}


	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}


	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}


	public Date getAlteradoEmMbcAgenda() {
		return alteradoEmMbcAgenda;
	}


	public Date getDtHrInclusao() {
		return dtHrInclusao;
	}


	public Short getSerVinCodigo() {
		return serVinCodigo;
	}


	public Integer getSerMatricula() {
		return serMatricula;
	}


	public Short getSerVinCodigoAlteradoPor() {
		return serVinCodigoAlteradoPor;
	}


	public Integer getSerMatriculaAlteradoPor() {
		return serMatriculaAlteradoPor;
	}


	public String getLtoLtoId() {
		return ltoLtoId;
	}


	public Short getQrtNumero() {
		return qrtNumero;
	}


	public Short getUnfSeq() {
		return unfSeq;
	}


	public String getCidCodigo() {
		return cidCodigo;
	}


	public String getCidDescricao() {
		return cidDescricao;
	}


	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}


	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}


	public void setAlteradoEmMbcAgenda(Date alteradoEmMbcAgenda) {
		this.alteradoEmMbcAgenda = alteradoEmMbcAgenda;
	}


	public void setDtHrInclusao(Date dtHrInclusao) {
		this.dtHrInclusao = dtHrInclusao;
	}


	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}


	public void setSerVinCodigoAlteradoPor(Short serVinCodigoAlteradoPor) {
		this.serVinCodigoAlteradoPor = serVinCodigoAlteradoPor;
	}


	public void setSerMatriculaAlteradoPor(Integer serMatriculaAlteradoPor) {
		this.serMatriculaAlteradoPor = serMatriculaAlteradoPor;
	}


	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}


	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}


	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}


	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}


	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}


	public Date getDtAgendaDate() {
		return dtAgendaDate;
	}


	public Date getTempoSalaDate() {
		return tempoSalaDate;
	}


	public DominioSexo getSexoPacDominio() {
		return sexoPacDominio;
	}


	public Short getQtdeHemoShort() {
		return qtdeHemoShort;
	}


	public Short getQtdeHemoAdicShort() {
		return qtdeHemoAdicShort;
	}


	public DominioLadoCirurgiaAgendas getLadoCirugiaDominio() {
		return ladoCirugiaDominio;
	}


	public void setDtAgendaDate(Date dtAgendaDate) {
		this.dtAgendaDate = dtAgendaDate;
	}


	public void setTempoSalaDate(Date tempoSalaDate) {
		this.tempoSalaDate = tempoSalaDate;
	}


	public void setSexoPacDominio(DominioSexo sexoPacDominio) {
		this.sexoPacDominio = sexoPacDominio;
	}


	public void setQtdeHemoShort(Short qtdeHemoShort) {
		this.qtdeHemoShort = qtdeHemoShort;
	}


	public void setQtdeHemoAdicShort(Short qtdeHemoAdicShort) {
		this.qtdeHemoAdicShort = qtdeHemoAdicShort;
	}


	public void setLadoCirugiaDominio(DominioLadoCirurgiaAgendas ladoCirugiaDominio) {
		this.ladoCirugiaDominio = ladoCirugiaDominio;
	}


	public List<LinhaReportVO> getHemoterapicosList() {
		return hemoterapicosList;
	}


	public void setHemoterapicosList(List<LinhaReportVO> hemoterapicosList) {
		this.hemoterapicosList = hemoterapicosList;
	}


	public Integer getProntuarioInteger() {
		return prontuarioInteger;
	}


	public void setProntuarioInteger(Integer prontuarioInteger) {
		this.prontuarioInteger = prontuarioInteger;
	}


	
}
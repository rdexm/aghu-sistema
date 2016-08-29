package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioBoletimAmbulatorio;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.core.commons.BaseBean;


public class FatArqEspelhoProcedAmbVO implements BaseBean {

	private static final long serialVersionUID = 5783292400237424662L;
	
	private DominioModuloCompetencia modulo;
	private Integer mes;
	private Integer ano;
	private Date dtHrInicio;

	private Short unfSeq;
	private String unfDesc;
	private String item;
	
	private Long qtOK;
	private Long qtNOK;
	
	private Short phoSeq;
	private Integer seq;
	private String descricaoItem;
	private Long codTabela;

	private Long qtIndConsistente1;
	private Long qtIndConsistente2;

	private BigDecimal vlProc;
	private BigDecimal vlServProf;
	private BigDecimal vlAnestesia;

	private String codigoUps;
	private Integer competencia;
	private String codAtvProf;
	private Short folha;
	private Byte linha;
	private Long procedimentoHosp;
	private Long cnsPaciente;
	private Long cnsMedico;
	private String nomePaciente;
	private Date dtNascimento;
	private DominioSexo sexo;
	private Integer codIbge;
	private Date dataAtendimento;
	private String cid10;
	private Short idade;
	private Integer quantidade;
	private Byte caraterAtendimento;
	private Long nroAutorizacao;
	private DominioBoletimAmbulatorio origemInf;
	private Byte raca;
	private Integer codigoNacionalidade;
	

	private Byte tipoAtendimento;
	private Byte grupoAtendimento;
	private Byte faixaEtaria;
	
	private String servico;
	private String classificacao;
	private String procedimentoHospFormatado;
	private Byte atvProfissional;
	

	private Integer endCepPaciente;
	private Integer endCodLogradouroPaciente; 
	private Integer endCodLogradouroPacienteBackup; 
	private String endLogradouroPaciente; 
	private String endComplementoPaciente;
	private Integer endNumeroPaciente; 
	private String endBairroPaciente; 
	
	public FatArqEspelhoProcedAmbVO() {}
	public FatArqEspelhoProcedAmbVO(final Short unfSeq,   final String unfDesc, 
									final Long codTabela, final String descricaoItem,
									final Long qtOK,   	  final Long qtNOK,
									final BigDecimal vlProc, final BigDecimal vlServProf, final BigDecimal vlAnestesia
									) {
		this.unfSeq  = unfSeq;
		this.unfDesc = unfDesc;
		
		this.codTabela 	  = codTabela;
		this.descricaoItem = descricaoItem;

		this.qtOK  = qtOK;
		this.qtNOK = qtNOK;

		this.vlProc      = vlProc;
		this.vlServProf  = vlServProf;
		this.vlAnestesia = vlAnestesia;
	}
	
	
	public enum Fields {
		CPE_DT_HR_INICIO("dtHrInicio"),
		CPE_MODULO("modulo"),
		CPE_MES("mes"),
		CPE_ANO("ano"),
		PROCEDIMENTO_HOSP("procedimentoHosp"),
		ORIGEM_INF("origemInf"),
		UNF_SEQ("unfSeq"),
		UNF_DESC("unfDesc"),
		PHO_SEQ("phoSeq"),
		SEQ("seq"),
		DESCRICAO_ITEM("descricaoItem"),
		QT_IND_CONSISTENTE1("qtIndConsistente1"),
		QT_IND_CONSISTENTE2("qtIndConsistente2"),
		
		VL_PROC("vlProc"),
		VL_SERV_PROF("vlServProf"),
		VL_ANESTESIA("vlAnestesia"),
		
		CODIGO_UPS("codigoUps"),
		COMPETENCIA("competencia"),
		COD_ATV_PROF("codAtvProf"),
		FOLHA("folha"),
		LINHA("linha"),
		CNS_PACIENTE("cnsPaciente"),
		CNS_MEDICO("cnsMedico"),
		NOME_PACIENTE("nomePaciente"),
		DT_NASCIMENTO("dtNascimento"),
		SEXO("sexo"),
		COD_IBGE("codIbge"),
		DATA_ATENDIMENTO("dataAtendimento"),
		CID_10("cid10"),
		IDADE("idade"),
		QUANTIDADE("quantidade"),
		CARATER_ATENDIMENTO("caraterAtendimento"),
		NRO_AUTORIZACAO("nroAutorizacao"),
		RACA("raca"),
		CODIGO_NACIONALIDADE("codigoNacionalidade"),
		
		ITEM("item"),
		QT_OK("qtOK"),
		QT_NOK("qtNOK"),

		COD_TABELA("codTabela"),

		FAIXA_ETARIA("faixaEtaria"),
		TIPO_ATENDIMENTO("tipoAtendimento"),
		GRUPO_ATENDIMENTO("grupoAtendimento"),
		
		CLASSIFICACAO("classificacao"),
		SERVICO("servico"),
		PROCEDIMENTO_HOSP_FORMATADO("procedimentoHospFormatado"),
		ATV_PROFISSIONAL("atvProfissional"),

		END_CEP_PACIENTE("endCepPaciente"),
		END_COD_LOGRADOURO_PACIENTE("endCodLogradouroPaciente"),
		END_COD_LOGRADOURO_PACIENTE_BACKUP("endCodLogradouroPacienteBackup"),
		END_LOGRADOURO_PACIENTE("endLogradouroPaciente"),
		END_COMPLEMENTO_PACIENTE("endComplementoPaciente"),
		END_NUMERO_PACIENTE("endNumeroPaciente"),
		END_BAIRRO_PACIENTE("endBairroPaciente"),
		
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}	
	
	public Integer getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Integer competencia) {
		this.competencia = competencia;
	}

	public DominioModuloCompetencia getModulo() {
		return modulo;
	}

	public void setModulo(DominioModuloCompetencia modulo) {
		this.modulo = modulo;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricaoItem() {
		return descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	public Long getProcedimentoHosp() {
		return procedimentoHosp;
	}

	public void setProcedimentoHosp(Long procedimentoHosp) {
		this.procedimentoHosp = procedimentoHosp;
	}

	public Long getQtIndConsistente1() {
		return qtIndConsistente1;
	}

	public void setQtIndConsistente1(Long qtIndConsistente1) {
		this.qtIndConsistente1 = qtIndConsistente1;
	}

	public Long getQtIndConsistente2() {
		return qtIndConsistente2;
	}

	public void setQtIndConsistente2(Long qtIndConsistente2) {
		this.qtIndConsistente2 = qtIndConsistente2;
	}

	public BigDecimal getVlProc() {
		return vlProc;
	}

	public void setVlProc(BigDecimal vlProc) {
		this.vlProc = vlProc;
	}

	public BigDecimal getVlServProf() {
		return vlServProf;
	}

	public void setVlServProf(BigDecimal vlServProf) {
		this.vlServProf = vlServProf;
	}

	public BigDecimal getVlAnestesia() {
		return vlAnestesia;
	}

	public void setVlAnestesia(BigDecimal vlAnestesia) {
		this.vlAnestesia = vlAnestesia;
	}

	public String getCodigoUps() {
		return codigoUps;
	}

	public void setCodigoUps(String codigoUps) {
		this.codigoUps = codigoUps;
	}

	public String getCodAtvProf() {
		return codAtvProf;
	}

	public void setCodAtvProf(String codAtvProf) {
		this.codAtvProf = codAtvProf;
	}

	public Short getFolha() {
		return folha;
	}

	public void setFolha(Short folha) {
		this.folha = folha;
	}

	public Byte getLinha() {
		return linha;
	}

	public void setLinha(Byte linha) {
		this.linha = linha;
	}

	public Long getCnsPaciente() {
		return cnsPaciente;
	}

	public void setCnsPaciente(Long cnsPaciente) {
		this.cnsPaciente = cnsPaciente;
	}

	public Long getCnsMedico() {
		return cnsMedico;
	}

	public void setCnsMedico(Long cnsMedico) {
		this.cnsMedico = cnsMedico;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public Integer getCodIbge() {
		return codIbge;
	}

	public void setCodIbge(Integer codIbge) {
		this.codIbge = codIbge;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public String getCid10() {
		return cid10;
	}

	public void setCid10(String cid10) {
		this.cid10 = cid10;
	}

	public Short getIdade() {
		return idade;
	}

	public void setIdade(Short idade) {
		this.idade = idade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Byte getCaraterAtendimento() {
		return caraterAtendimento;
	}

	public void setCaraterAtendimento(Byte caraterAtendimento) {
		this.caraterAtendimento = caraterAtendimento;
	}

	public Long getNroAutorizacao() {
		return nroAutorizacao;
	}

	public void setNroAutorizacao(Long nroAutorizacao) {
		this.nroAutorizacao = nroAutorizacao;
	}

	public DominioBoletimAmbulatorio getOrigemInf() {
		return origemInf;
	}

	public void setOrigemInf(DominioBoletimAmbulatorio origemInf) {
		this.origemInf = origemInf;
	}

	public Byte getRaca() {
		return raca;
	}

	public void setRaca(Byte raca) {
		this.raca = raca;
	}

	public Integer getCodigoNacionalidade() {
		return codigoNacionalidade;
	}

	public void setCodigoNacionalidade(Integer codigoNacionalidade) {
		this.codigoNacionalidade = codigoNacionalidade;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Long getQtOK() {
		return qtOK;
	}

	public void setQtOK(Long qtOK) {
		this.qtOK = qtOK;
	}

	public Long getQtNOK() {
		return qtNOK;
	}

	public void setQtNOK(Long qtNOK) {
		this.qtNOK = qtNOK;
	}

	public String getUnfDesc() {
		return unfDesc;
	}

	public void setUnfDesc(String unfDesc) {
		this.unfDesc = unfDesc;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}
	
	public Byte getTipoAtendimento() {
		return tipoAtendimento;
	}
	
	public void setTipoAtendimento(Byte tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}
	
	public Byte getGrupoAtendimento() {
		return grupoAtendimento;
	}
	
	public void setGrupoAtendimento(Byte grupoAtendimento) {
		this.grupoAtendimento = grupoAtendimento;
	}
	
	public Byte getFaixaEtaria() {
		return faixaEtaria;
	}
	
	public void setFaixaEtaria(Byte faixaEtaria) {
		this.faixaEtaria = faixaEtaria;
	}
	
	public String getServico() {
		return servico;
	}
	
	public void setServico(String servico) {
		this.servico = servico;
	}
	
	public String getClassificacao() {
		return classificacao;
	}
	
	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}
	
	public String getProcedimentoHospFormatado() {
		return procedimentoHospFormatado;
	}
	
	public void setProcedimentoHospFormatado(String procedimentoHospFormatado) {
		this.procedimentoHospFormatado = procedimentoHospFormatado;
	}
	
	public Byte getAtvProfissional() {
		return atvProfissional;
	}
	
	public void setAtvProfissional(Byte atvProfissional) {
		this.atvProfissional = atvProfissional;
	}
	public Integer getEndCepPaciente() {
		return endCepPaciente;
	}
	public void setEndCepPaciente(Integer endCepPaciente) {
		this.endCepPaciente = endCepPaciente;
	}
	public Integer getEndCodLogradouroPaciente() {
		return endCodLogradouroPaciente;
	}
	public void setEndCodLogradouroPaciente(Integer endCodLogradouroPaciente) {
		this.endCodLogradouroPaciente = endCodLogradouroPaciente;
	}
	public String getEndLogradouroPaciente() {
		return endLogradouroPaciente;
	}
	public void setEndLogradouroPaciente(String endLogradouroPaciente) {
		this.endLogradouroPaciente = endLogradouroPaciente;
	}
	public String getEndComplementoPaciente() {
		return endComplementoPaciente;
	}
	public void setEndComplementoPaciente(String endComplementoPaciente) {
		this.endComplementoPaciente = endComplementoPaciente;
	}
	public Integer getEndNumeroPaciente() {
		return endNumeroPaciente;
	}
	public void setEndNumeroPaciente(Integer endNumeroPaciente) {
		this.endNumeroPaciente = endNumeroPaciente;
	}
	public String getEndBairroPaciente() {
		return endBairroPaciente;
	}
	public void setEndBairroPaciente(String endBairroPaciente) {
		this.endBairroPaciente = endBairroPaciente;
	}
	public Integer getEndCodLogradouroPacienteBackup() {
		return endCodLogradouroPacienteBackup;
	}
	public void setEndCodLogradouroPacienteBackup(
			Integer endCodLogradouroPacienteBackup) {
		this.endCodLogradouroPacienteBackup = endCodLogradouroPacienteBackup;
	}
	
	
}
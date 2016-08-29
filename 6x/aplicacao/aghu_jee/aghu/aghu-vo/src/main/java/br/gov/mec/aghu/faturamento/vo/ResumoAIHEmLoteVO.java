package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.commons.CoreUtil;


public class ResumoAIHEmLoteVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6434805928533023942L;
	private Integer cthSeq;
	private Integer cthSeqReapresentada;
	private Integer seqp;
	private Integer prontuario;
	private Integer codPaciente;
	private String nome;
	private Date dtNascimento;
	private String pacSexo;
	private Integer dv;
	private Byte dciCpeMes;
	private Short dciCpeAno;
	private String dtApresentacao;
	private String codigoDcih;
	private String exclusaoCritica;
	private Integer conCodCentral;
	private String dauSenha;
	private Integer cerih;
	private Long numeroAih;
	private Short tahSeq;
	private Byte especialidadeAih;
	private String especialidade;	
	private String leito;
	private String enfermaria;
//	private String enfermariaLeito;
	private Byte tciCodSus;
	private Long cpfMedicoAuditor;
//	private String cpfMedicoAuditorFormatado;
	private Date dtInternacao;
	private Date dtSaida;
	private String motivoCobranca;
	private String cidPrimario;
	private String cidSecundario;
	private Long numeroAihAnterior;
	private Long numeroAihPosterior;
	private Long iphCodSusSolic;
	private Long iphCodSusRealz;
	private Short phoSeqRealz;
	private Integer seqRealz;
	private String descricaoProcedimentoSolic;
	private String descricaoProcedimentoRealz;
	private BigDecimal valorSH;
	private BigDecimal valorSP;
	private BigDecimal valorSADT;
	private BigDecimal valorAcomp;
	private BigDecimal valorTotal;
	private String usuarioPrevia;
	private Date dtPrevia;
	private String criadoPor;
	private Date criadoEm;
	private String detalhe1;
	private String detalhe2;
	private String detalhe3;
	private String reimpressao;
	private BigDecimal totalSH;
	private BigDecimal totalSP;
	private BigDecimal totalSADT;
	private BigDecimal total;
	private Boolean indImprimeEspelho;
	private String strCriadoEm;
	private String strDtPrevia;
	private Long cnsMedicoAuditor;
	private String cpfOuCnsMedicoAuditor;
	private String nroCartaoSaude;
	private String prontuarioFormatado;
	private List<ResumoAIHEmLoteServicosVO> listaServicos;	
	
	public enum Fields {
		CTH_SEQ("cthSeq"),
		CTH_SEQ_REAPRESENTADA("cthSeqReapresentada"),
		SEQ_P("seqp"),
		PRONTUARIO("prontuario"),
		COD_PACIENTE("codPaciente"),
		NOME("nome"),
		DT_NASCIMENTO("dtNascimento"),
		PAC_SEXO("pacSexo"),
		DV("dv"),
		DCI_CPE_MES("dciCpeMes"),
		DCI_CPE_ANO("dciCpeAno"),
		DT_APRESENTACAO("dtApresentacao"),
		CODIGO_DCIH("codigoDcih"),
		EXCLUSAO_CRITICA("exclusaoCritica"),
		CON_COD_CENTRAL("conCodCentral"),
		DAU_SENHA("dauSenha"),
		CERIH("cerih"),
		NUMERO_AIH("numeroAih"),
		TAH_SEQ("tahSeq"),
		ESPECIALIDADE_AIH("especialidadeAih"),
		ESPECIALIDADE("especialidade"),
		LEITO("leito"),
		ENFERMARIA("enfermaria"),
		ENFERMARIA_LEITO("enfermariaLeito"),
		TCI_COD_SUS("tciCodSus"),
		CPF_MEDICO_AUDITOR("cpfMedicoAuditor"),
		CPF_MEDICO_AUDITOR_FORMATADO("cpfMedicoAuditorFormatado"),
		DT_INTERNACAO("dtInternacao"),
		DT_SAIDA("dtSaida"),
		MOTIVO_COBRANCA("motivoCobranca"),
		CID_PRIMARIO("cidPrimario"),
		CID_SECUNDARIO("cidSecundario"),
		NUMERO_AIH_ANTERIOR("numeroAihAnterior"),
		NUMERO_AIH_POSTERIOR("numeroAihPosterior"),
		IPH_COD_SUS_SOLIC("iphCodSusSolic"),
		IPH_COD_SUS_REALZ("iphCodSusRealz"),
		PHO_SEQ_REALZ("phoSeqRealz"),
		SEQ_REALZ("seqRealz"),
		DESCRICAO_PROCEDIMENTO_SOLIC("descricaoProcedimentoSolic"),
		DESCRICAO_PROCEDIMENTO_REALZ("descricaoProcedimentoRealz"),
		VALOR_SH("valorSH"),
		VALOR_SP("valorSP"),
		VALOR_SADT("valorSADT"),
		VALOR_ACOMP("valorAcomp"),
		VALOR_TOTAL("valorTotal"),
		USUARIO_PREVIA("usuarioPrevia"),
		DT_PREVIA("dtPrevia"),
		CRIADO_POR("criadoPor"),
		CRIADO_EM("criadoEm"),
		DETALHE1("detalhe1"),
		DETALHE2("detalhe2"),
		DETALHE3("detalhe3"),
		REIMPRESSAO("reimpressao"),
		TOTAL_SH("totalSH"),
		TOTAL_SP("totalSP"),
		TOTAL_SADT("totalSADT"),
		TOTAL("total"),
		IND_IMPRIME_ESPELHO("indImprimeEspelho");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

	}
	
	
	
	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public Integer getCerih() {
		return cerih;
	}

	public void setCerih(Integer cerih) {
		this.cerih = cerih;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public String getPacSexo() {
		return pacSexo;
	}

	public void setPacSexo(String pacSexo) {
		this.pacSexo = pacSexo;
	}
	
	public Integer getCthSeqReapresentada() {
		return cthSeqReapresentada;
	}

	public void setCthSeqReapresentada(Integer cthSeqReapresentada) {
		this.cthSeqReapresentada = cthSeqReapresentada;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getDv() {
		return dv;
	}

	public void setDv(Integer dv) {
		this.dv = dv;
	}

	public String getDtApresentacao() {
		return dtApresentacao;
	}

	public void setDtApresentacao(String dtApresentacao) {
		this.dtApresentacao = dtApresentacao;
	}

	public String getCodigoDcih() {
		return codigoDcih;
	}

	public void setCodigoDcih(String codigoDcih) {
		this.codigoDcih = codigoDcih;
	}

	public String getExclusaoCritica() {
//		if("1".equals(exclusaoCritica)) {
//			return "1 - Permanência menor que a estabelecida";
//		}
//		else if("2".equals(exclusaoCritica)) {
//			return "2 - Paciente com idade menor que a idade mínima";
//		}
//		else if("3".equals(exclusaoCritica)) {
//			return "3 - Paciente com idade maior que a idade máxima";
//		}
		return exclusaoCritica;
	}

	public void setExclusaoCritica(String exclusaoCritica) {
		this.exclusaoCritica = exclusaoCritica;
	}

	public Integer getConCodCentral() {
		return conCodCentral;
	}

	public void setConCodCentral(Integer conCodCentral) {
		this.conCodCentral = conCodCentral;
	}

	public String getDauSenha() {
		return dauSenha;
	}

	public void setDauSenha(String dauSenha) {
		this.dauSenha = dauSenha;
	}

	public Long getNumeroAih() {
		return numeroAih;
	}

	public void setNumeroAih(Long numeroAih) {
		this.numeroAih = numeroAih;
	}

	public Short getTahSeq() {
		return tahSeq;
	}

	public void setTahSeq(Short tahSeq) {
		this.tahSeq = tahSeq;
	}
	
	public Byte getEspecialidadeAih() {
		return especialidadeAih;
	}

	public void setEspecialidadeAih(Byte especialidadeAih) {
		this.especialidadeAih = especialidadeAih;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getEnfermaria() {
		return enfermaria;
	}

	public void setEnfermaria(String enfermaria) {
		this.enfermaria = enfermaria;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}
	
	public String getEnfermariaLeito() {
		return ((enfermaria!=null)?enfermaria:"") + ((leito!=null)?leito:"");
	}

//	public void setEnfermariaLeito(String enfermariaLeito) {
//		this.enfermariaLeito = enfermariaLeito;
//	}

	public Byte getTciCodSus() {
		return tciCodSus;
	}

	public void setTciCodSus(Byte tciCodSus) {
		this.tciCodSus = tciCodSus;
	}

	public Long getCpfMedicoAuditor() {
		return cpfMedicoAuditor;
	}

	public void setCpfMedicoAuditor(Long cpfMedicoAuditor) {
		this.cpfMedicoAuditor = cpfMedicoAuditor;
	}
	
	public String getCpfMedicoAuditorFormatado() {
		return (cpfMedicoAuditor!=null)?CoreUtil.formataCPF(cpfMedicoAuditor):null;
	}

//	public void setCpfMedicoAuditorFormatado(String cpfMedicoAuditorFormatado) {
//		this.cpfMedicoAuditorFormatado = cpfMedicoAuditorFormatado;
//	}

	public Date getDtInternacao() {
		return dtInternacao;
	}

	public void setDtInternacao(Date dtInternacao) {
		this.dtInternacao = dtInternacao;
	}

	public Date getDtSaida() {
		return dtSaida;
	}

	public void setDtSaida(Date dtSaida) {
		this.dtSaida = dtSaida;
	}

	public String getMotivoCobranca() {
		return motivoCobranca;
	}

	public void setMotivoCobranca(String motivoCobranca) {
		this.motivoCobranca = motivoCobranca;
	}

	public String getCidPrimario() {
		return cidPrimario;
	}

	public void setCidPrimario(String cidPrimario) {
		this.cidPrimario = cidPrimario;
	}

	public String getCidSecundario() {
		return cidSecundario;
	}

	public void setCidSecundario(String cidSecundario) {
		this.cidSecundario = cidSecundario;
	}

	public Long getNumeroAihAnterior() {
		return numeroAihAnterior;
	}

	public void setNumeroAihAnterior(Long numeroAihAnterior) {
		this.numeroAihAnterior = numeroAihAnterior;
	}

	public Long getNumeroAihPosterior() {
		return numeroAihPosterior;
	}

	public void setNumeroAihPosterior(Long numeroAihPosterior) {
		this.numeroAihPosterior = numeroAihPosterior;
	}

	public Long getIphCodSusSolic() {
		return iphCodSusSolic;
	}

	public void setIphCodSusSolic(Long iphCodSusSolic) {
		this.iphCodSusSolic = iphCodSusSolic;
	}
	
	public Long getIphCodSusRealz() {
		return iphCodSusRealz;
	}

	public void setIphCodSusRealz(Long iphCodSusRealz) {
		this.iphCodSusRealz = iphCodSusRealz;
	}
	
	public Integer getSeqRealz() {
		return seqRealz;
	}

	public void setSeqRealz(Integer seqRealz) {
		this.seqRealz = seqRealz;
	}

	public Short getPhoSeqRealz() {
		return phoSeqRealz;
	}

	public void setPhoSeqRealz(Short phoSeqRealz) {
		this.phoSeqRealz = phoSeqRealz;
	}

	public String getDescricaoProcedimentoSolic() {
		return descricaoProcedimentoSolic;
	}

	public void setDescricaoProcedimentoSolic(String descricaoProcedimentoSolic) {
		this.descricaoProcedimentoSolic = descricaoProcedimentoSolic;
	}
	
	public String getDescricaoProcedimentoRealz() {
		return descricaoProcedimentoRealz;
	}

	public void setDescricaoProcedimentoRealz(String descricaoProcedimentoRealz) {
		this.descricaoProcedimentoRealz = descricaoProcedimentoRealz;
	}

	public BigDecimal getValorSH() {
		return valorSH;
	}

	public void setValorSH(BigDecimal valorSH) {
		this.valorSH = valorSH;
	}

	public BigDecimal getValorSP() {
		return valorSP;
	}

	public void setValorSP(BigDecimal valorSP) {
		this.valorSP = valorSP;
	}

	public BigDecimal getValorSADT() {
		return valorSADT;
	}

	public void setValorSADT(BigDecimal valorSADT) {
		this.valorSADT = valorSADT;
	}	
	
	public BigDecimal getValorAcomp() {
		return valorAcomp;
	}

	public void setValorAcomp(BigDecimal valorAcomp) {
		this.valorAcomp = valorAcomp;
	}

	public BigDecimal getValorTotal() {
		valorTotal = BigDecimal.ZERO ;
		
		if(valorSH!=null) {
			valorTotal = valorTotal.add(valorSH); 
		} 
		if(valorSP!=null) {
			valorTotal = valorTotal.add(valorSP); 
		} 

		if(valorSADT!=null) {
			valorTotal = valorTotal.add(valorSADT); 
		} 

		if(valorAcomp!=null) {
			valorTotal = valorTotal.add(valorAcomp); 
		} 
		
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}	
	
	public Byte getDciCpeMes() {
		return dciCpeMes;
	}

	public void setDciCpeMes(Byte dciCpeMes) {
		this.dciCpeMes = dciCpeMes;
	}

	public Short getDciCpeAno() {
		return dciCpeAno;
	}

	public void setDciCpeAno(Short dciCpeAno) {
		this.dciCpeAno = dciCpeAno;
	}
	
	public List<ResumoAIHEmLoteServicosVO> getListaServicos() {
		return listaServicos;
	}

	public void setListaServicos(List<ResumoAIHEmLoteServicosVO> listaServicos) {
		this.listaServicos = listaServicos;
	}

	public String getUsuarioPrevia() {
		return usuarioPrevia;
	}

	public void setUsuarioPrevia(String usuarioPrevia) {
		this.usuarioPrevia = usuarioPrevia;
	}

	public Date getDtPrevia() {
		return dtPrevia;
	}

	public void setDtPrevia(Date dtPrevia) {
		this.dtPrevia = dtPrevia;
	}

	public String getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}
	
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getDetalhe1() {
		return detalhe1;
	}

	public void setDetalhe1(String detalhe1) {
		this.detalhe1 = detalhe1;
	}

	public String getDetalhe2() {
		return detalhe2;
	}

	public void setDetalhe2(String detalhe2) {
		this.detalhe2 = detalhe2;
	}

	public String getDetalhe3() {
		return detalhe3;
	}

	public void setDetalhe3(String detalhe3) {
		this.detalhe3 = detalhe3;
	}

	public String getReimpressao() {
		return reimpressao;
	}

	public void setReimpressao(String reimpressao) {
		this.reimpressao = reimpressao;
	}

	public BigDecimal getTotalSH() {
		totalSH = BigDecimal.ZERO;
		
		if(valorSH!=null) {
			totalSH = totalSH.add(valorSH); 
		} 
		if(valorAcomp!=null) {
			totalSH = totalSH.add(valorAcomp); 
		} 

		if(listaServicos != null){
			for(ResumoAIHEmLoteServicosVO servico : listaServicos) {
				if(servico.getValorsh() != null) {
					totalSH = totalSH.add(servico.getValorsh()); 
				}
			}
		}
		
		return totalSH;
	}

	public void setTotalSH(BigDecimal totalSH) {
		this.totalSH = totalSH;
	}

	public BigDecimal getTotalSP() {
		totalSP = BigDecimal.ZERO;
		
		if(valorSP!=null) {
			totalSP = totalSP.add(valorSP); 
		} 

		if(listaServicos != null){
			for(ResumoAIHEmLoteServicosVO servico : listaServicos) {
				if(servico.getValorsp() != null) {
					totalSP = totalSP.add(servico.getValorsp()); 
				}
			}
		}
		
		return totalSP;
	}

	public void setTotalSP(BigDecimal totalSP) {
		this.totalSP = totalSP;
	}

	public BigDecimal getTotalSADT() {
		totalSADT = BigDecimal.ZERO;
		
		if(valorSADT!=null) {
			totalSADT = totalSADT.add(valorSADT); 
		} 
		
		if(listaServicos != null){
			for(ResumoAIHEmLoteServicosVO servico : listaServicos) {
				if(servico.getValorsadt() != null) {
					totalSADT = totalSADT.add(servico.getValorsadt()); 
				}
			}
		}
		
		return totalSADT;
	}

	public void setTotalSADT(BigDecimal totalSADT) {
		this.totalSADT = totalSADT;
	}

	public BigDecimal getTotal() {
		total = BigDecimal.ZERO;

		if(valorSH!=null) {
			total = total.add(valorSH); 
		} 
		if(valorSP!=null) {
			total = total.add(valorSP); 
		} 

		if(valorSADT!=null) {
			total = total.add(valorSADT); 
		} 

		if(valorAcomp!=null) {
			total = total.add(valorAcomp); 
		} 

		if(listaServicos != null){
			for(ResumoAIHEmLoteServicosVO servico : listaServicos) {
				if(servico.getValortotal() != null) {
					total = total.add(servico.getValortotal()); 
				}
			}
		}
		
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Boolean getIndImprimeEspelho() {
		return indImprimeEspelho;
	}

	public void setIndImprimeEspelho(Boolean indImprimeEspelho) {
		this.indImprimeEspelho = indImprimeEspelho;
	}
	
	public String getStrCriadoEm() {
		return strCriadoEm;
	}

	public void setStrCriadoEm(String strCriadoEm) {
		this.strCriadoEm = strCriadoEm;
	}

	public String getStrDtPrevia() {
		return strDtPrevia;
	}

	public void setStrDtPrevia(String strDtPrevia) {
		this.strDtPrevia = strDtPrevia;
	}

	public Long getCnsMedicoAuditor() {
		return cnsMedicoAuditor;
	}

	public void setCnMedicoAuditor(Long cnsMedicoAuditor) {
		this.cnsMedicoAuditor = cnsMedicoAuditor;
	}

	public String getCpfOuCnsMedicoAuditor() {
		return cpfOuCnsMedicoAuditor;
	}

	public void setCpfOuCnsMedicoAuditor(String cpfOuCnsMedicoAuditor) {
		this.cpfOuCnsMedicoAuditor = cpfOuCnsMedicoAuditor;
	}

	public String getNroCartaoSaude() {
		return nroCartaoSaude;
	}

	public void setNroCartaoSaude(String nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}
	
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}
}

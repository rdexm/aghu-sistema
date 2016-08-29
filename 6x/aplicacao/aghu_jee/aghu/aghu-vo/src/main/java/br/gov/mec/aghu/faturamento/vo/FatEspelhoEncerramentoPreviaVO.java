package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.core.utils.DateUtil;


public class FatEspelhoEncerramentoPreviaVO implements Serializable {

	private static final long serialVersionUID = 6565188592592651286L;

	private Long numeroAih;
	private DominioSituacaoConta indSituacao;
	private Byte dciCpeMes;
	private Short dciCpeAno;
	private Date aihDthrEmissao;
	private Byte especialidadeAih;
	private String descricaoEspecialidade;
	private String enfermaria;
	private String leito;
	private Long cpfMedicoAuditor;
	private String cnsMedicoAuditor;
	private Long cpfMedicoResponsavel;
	private String cnsMedicoResponsavel;
	private Long cpfMedicoSolic;
	private String cnsMedicoSolic;
	private BigInteger nroCartaoSaude;
	private String pacNome;
	private Integer pacProntuario;
	private Date pacDtNascimento;
	private String pacSexo;
	private Short nacionalidadePac;
	private Byte indDocPac;
	private String pacRG;
	private String nomeResponsavelPac;
	private String pacNomeMae;
	private String endLogradouroPac;
	private Integer endNroLogradouroPac;
	private String endCmplLogradouroPac;
	private String endBairroPac;
	private String pacCor;
	private Integer etniaId;
	private Integer codIbgeCidadePac;
	private String endCidadePac;
	private String endUfPac;
	private Integer endCepPac;
	private Short dddFoneResidencial;
	private Long foneResidencial;
	private Long iphCodSusSolic;
	private String descricaoIphs;
	private Long iphCodSusRealiz;
	private String descricaoIphr;
	private String cidPrimario;
	private String cidSecundario;
	private Byte saidasObitoCausa;
	private Byte tciCodSus;
	private Date dataInternacao;
	private Date dataSaida;
	private String motivoCobranca;
	private String exclusaoCritica;
	private String dauSenha;
	private Long numeroAihAnterior;
	private Long numeroAihPosterior;
	private Integer cthSeq;
	private Byte nascidosVivos;
	private Byte nascidosMortos;
	private Byte saidasAlta;
	private Byte saidasObito;
	private Byte saidasTransferencia;
	private Long nroSisPrenatal;
	private Integer cthSeqReapresentada;
	private String orgaoLocalRecebedor; //Parâmetro do sistema P_ORGAO_LOC_REC
	private String cpfDoutor; //Parâmetro do sistema P_CPF_DOUTOR
	private String cnsDoutor;
	private String cnes; //Parâmetro do sistema - concatenação de P_CNES_HCPA com P_HOSPITAL_RAZAO_SOCIAL 
	private String contaFormatada;
	private String descricaoNacionalidade;
	private String documento;
	private String descricaoEtnia;
	private String descricaoCid;
	private String caraterAtendimento;
	private String modalidade;
	private String motivoSaida;
	
	private Short iphPhoSeqRealiz;
	private Integer iphSeqRealiz;
	
	//ATRIBUTOS CRIADOS PARA EVITAR MÉTODOS QUE RETORNAM VALORES FORMATADOS
	private String numeroAihFormatado;
	private String especialidadeFormatada;
	private String sexoFormatado;
	private String nacionalidadeFormatada;
	private String tipoDocFormatado;
	private String enderecoFormatado;
	private String corFormatada;
	private String etniaFormatada;
	private String municipioFormatado;
	private String cepFormatado;
	private String telefoneFormatado;
	private String mudaProcFormatado;
	private String procedimentoSolicitadoFormatado;
	private String procedimentoPrincipalFormatado;
	private String diagnosticoPrincipalFormatado;
	private String dataApresentacaoFormatado;
	private String cartaoSaudeFormatado;
	private String prontuarioFormatado;
	
	private String versaoSisaih;
	
	public String getCartaoSaudeFormatado() {
		return cartaoSaudeFormatado;
	}

	public void setCartaoSaudeFormatado(String cartaoSaudeFormatado) {
		this.cartaoSaudeFormatado = cartaoSaudeFormatado;
	}

	public void formatarCampos(){
		formatarCep();
		formatarCor();
		formatarDataApresentacao();
		formatarDiagnosticoPrincipal();
		formatarDocumento();
		formatarEndereco();
		formatarEspecialidade();
		formatarEtnia();
		formatarMudaProc();
		formatarMunicipio();
		formatarNacionalidade();
		formatarNumeroAih();
		formatarProcedimentoPrincipal();
		formatarProcedimentoSolicitado();
		formatarSexo();
		formatarTelefone();
		formatarTipoDocumento();
		formatarNroCartaoSaude();
		formatarProntuario();
	}
	
	public void formatarProntuario() {
		if (this.pacProntuario != null) {
			//int tamanhoString = this.pacProntuario.toString().length();			
			//this.prontuarioFormatado = this.pacProntuario.toString().substring(0, tamanhoString-1).concat("/").concat(this.pacProntuario.toString().substring(tamanhoString-1, tamanhoString));
			
			// Formatação utilizada no código de barras
			this.prontuarioFormatado = StringUtils.leftPad(String.valueOf(this.pacProntuario), 9, '0');
			this.prontuarioFormatado = StringUtils.rightPad(this.prontuarioFormatado, 12, '0');
		}else{
			this.prontuarioFormatado = "";
		}
	}
	
	
	public void formatarNumeroAih() {
		if (this.numeroAih != null) {
			int tamanhoString = this.numeroAih.toString().length();			
			this.numeroAihFormatado = this.numeroAih.toString().substring(0, tamanhoString-1).concat("-").concat(this.numeroAih.toString().substring(tamanhoString-1, tamanhoString)); 
		}else{
			this.numeroAihFormatado = "";
		}
	}
	
	public void formatarEspecialidade() {		
		if (this.especialidadeAih != null && this.descricaoEspecialidade != null) {		
			this.especialidadeFormatada = this.especialidadeAih.toString().concat(" - ").concat(this.descricaoEspecialidade);		
		}else{
			this.especialidadeFormatada = "";
		}
	}	
	
	public void formatarSexo() {
		if (this.pacSexo!= null && this.pacSexo.equals("1")) {
			this.sexoFormatado = "Masculino";
		} else if (this.pacSexo!= null && this.pacSexo.equals("3")) {
			this.sexoFormatado = "Feminino";
		} else {
			this.sexoFormatado = "";
		}
	}
	
	public void formatarNacionalidade() {
		if (this.nacionalidadePac != null) {
			this.nacionalidadeFormatada = this.nacionalidadePac.toString().concat(" - ").concat(this.descricaoNacionalidade);
		}else{
			this.nacionalidadeFormatada = "";
		}
	}
	
	public void formatarTipoDocumento() {		
		if (this.indDocPac != null && this.indDocPac.equals(Byte.valueOf("1"))) {
			this.tipoDocFormatado = "CARTÃO NACIONAL DE SAÚDE";
		} else if (this.indDocPac != null && this.indDocPac.equals(Byte.valueOf("2"))) {
			this.tipoDocFormatado = "IDENTIDADE";
		} else {
			this.tipoDocFormatado = "";
		}
	}
	
	public void formatarEndereco() {
		this.enderecoFormatado = "";
		if (this.endLogradouroPac != null) {
			this.enderecoFormatado = this.endLogradouroPac;
		}		
		if (this.endNroLogradouroPac != null) {
			this.enderecoFormatado = this.enderecoFormatado.concat(" ").concat(this.endNroLogradouroPac.toString());
		}		
		if (this.endCmplLogradouroPac != null) {
			this.enderecoFormatado = this.enderecoFormatado.concat(" ").concat(this.endCmplLogradouroPac);
		}
	}
	
	public void formatarCor() {
		if (this.pacCor != null && !this.pacCor.isEmpty()) {
			if ("01".equals(this.pacCor) || "1".equals(this.pacCor)) {
				this.corFormatada = this.pacCor.concat("-").concat(DominioCor.B.getDescricao());
			} else if ("02".equals(this.pacCor) || "2".equals(this.pacCor)) {
				this.corFormatada = this.pacCor.concat("-").concat(DominioCor.P.getDescricao());
			} else if ("03".equals(this.pacCor) || "3".equals(this.pacCor)) {
				this.corFormatada = this.pacCor.concat("-").concat(DominioCor.M.getDescricao());
			} else if ("04".equals(this.pacCor) || "4".equals(this.pacCor)) {
				this.corFormatada = this.pacCor.concat("-").concat(DominioCor.A.getDescricao());
			} else if ("05".equals(this.pacCor) || "5".equals(this.pacCor)) {
				this.corFormatada = this.pacCor.concat("-").concat(DominioCor.I.getDescricao());
			} else if ("99".equals(this.pacCor)) {
				this.corFormatada = DominioCor.O.getDescricao();			
			} else {
				this.corFormatada = "";
			}
		}
	}
	
	public void formatarEtnia() {
		if (this.etniaId == null){
			this.etniaFormatada = "0000-NÃO SE APLICA";
		} else {
			this.etniaFormatada = this.etniaId.toString().concat("-").concat(this.descricaoEtnia);
		}
	}
	
	public void formatarMunicipio() {
		if (this.codIbgeCidadePac != null && this.endCidadePac != null) {
			this.municipioFormatado = this.codIbgeCidadePac.toString().concat("-").concat(this.endCidadePac);
		}else{
			
			this.municipioFormatado = "";
		}
	}

	public void formatarCep(){		
		if (this.endCepPac != null) {	        
			String cep = this.endCepPac.toString();
			String parte1, parte2;
	        while (cep.length() < 8) {
	        	cep = "0".concat(cep);
	        }	          
	        parte1 = cep.substring(0, 5);
	        parte2 = cep.substring(5);
	        this.cepFormatado = parte1.concat("-").concat(parte2);
		}else{
			this.cepFormatado = "";
		}
	}
	
	public void formatarTelefone() {				
		if (this.dddFoneResidencial != null && foneResidencial != null) {	        
			this.telefoneFormatado = "(".concat(this.dddFoneResidencial.toString()).concat(")").concat(this.foneResidencial.toString());
		}else{
			this.telefoneFormatado = "";
		}
		
	}
	
	public void formatarMudaProc() {		
		if (this.iphCodSusSolic != null && this.iphCodSusRealiz != null) {
			if (this.iphCodSusSolic.equals(this.iphCodSusRealiz)) {
				this.mudaProcFormatado =  "NÃO";
			} else {
				this.mudaProcFormatado = "SIM";
			}
		}else{
			this.mudaProcFormatado = "";
		}
	}
	
	public void formatarProcedimentoSolicitado() {
		if (iphCodSusSolic != null && descricaoIphs != null) {
			this.procedimentoSolicitadoFormatado = iphCodSusSolic.toString().concat("-").concat(descricaoIphs);
		}else{
			this.procedimentoSolicitadoFormatado = "";
		}		
	}
	
	public void formatarProcedimentoPrincipal() {		
		if (iphCodSusRealiz != null && descricaoIphr != null) {
			this.procedimentoPrincipalFormatado = iphCodSusRealiz.toString().concat("-").concat(descricaoIphr);
		}else{
			this.procedimentoPrincipalFormatado = "";
		}
	}
	
	public void formatarDiagnosticoPrincipal() {
		if (this.cidPrimario != null) {
			this.diagnosticoPrincipalFormatado = this.cidPrimario.concat("-").concat(this.descricaoCid);
		}else{
			this.diagnosticoPrincipalFormatado = "";
		}
		
	}

	public void formatarDataApresentacao() {
		if (this.dciCpeAno != null && this.dciCpeMes != null) {
			Date dataApresentacaoFormatada = new Date();
			dataApresentacaoFormatada = DateUtil.adicionaMeses(DateUtil.obterData(this.dciCpeAno, this.dciCpeMes - 1, 1),1);
			this.dataApresentacaoFormatado = DateUtil.obterDataFormatada(dataApresentacaoFormatada,"MM/yyyy");
		}else{
			this.dataApresentacaoFormatado = "";
		}
	}
	
	public void formatarDocumento() {	
		this.documento = this.pacRG;
	}
	
	public void formatarConta(Short valorF2){
		
		this.contaFormatada = this.getCthSeq().toString().concat("-").concat(valorF2.toString());
	}
	
	public void formatarNroCartaoSaude() {

		if (this.nroCartaoSaude != null) {

			int tamanhoString = this.nroCartaoSaude.toString().length();

			this.cartaoSaudeFormatado = this.nroCartaoSaude.toString().substring(0,tamanhoString - 1)
					.concat("-").concat(this.nroCartaoSaude.toString().substring(tamanhoString - 1, tamanhoString));
		}else{
			
			this.cartaoSaudeFormatado = "";
		}
	}
	
	
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public Long getNumeroAih() {
		return numeroAih;
	}
	public void setNumeroAih(Long numeroAih) {
		this.numeroAih = numeroAih;
	}
	public DominioSituacaoConta getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacaoConta indSituacao) {
		this.indSituacao = indSituacao;
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
	public Date getAihDthrEmissao() {
		return aihDthrEmissao;
	}
	public void setAihDthrEmissao(Date aihDthrEmissao) {
		this.aihDthrEmissao = aihDthrEmissao;
	}
	public Byte getEspecialidadeAih() {
		return especialidadeAih;
	}
	public void setEspecialidadeAih(Byte especialidadeAih) {
		this.especialidadeAih = especialidadeAih;
	}
	public String getDescricaoEspecialidade() {
		return descricaoEspecialidade;
	}
	public void setDescricaoEspecialidade(String descricaoEspecialidade) {
		this.descricaoEspecialidade = descricaoEspecialidade;
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
	public Long getCpfMedicoAuditor() {
		return cpfMedicoAuditor;
	}
	public void setCpfMedicoAuditor(Long cpfMedicoAuditor) {
		this.cpfMedicoAuditor = cpfMedicoAuditor;
	}
	public Long getCpfMedicoResponsavel() {
		return cpfMedicoResponsavel;
	}
	public void setCpfMedicoResponsavel(Long cpfMedicoResponsavel) {
		this.cpfMedicoResponsavel = cpfMedicoResponsavel;
	}
	public Long getCpfMedicoSolic() {
		return cpfMedicoSolic;
	}
	public void setCpfMedicoSolic(Long cpfMedicoSolic) {
		this.cpfMedicoSolic = cpfMedicoSolic;
	}
	public BigInteger getNroCartaoSaude() {
		return nroCartaoSaude;
	}
	public void setNroCartaoSaude(BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}
	public String getPacNome() {
		return pacNome;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public Integer getPacProntuario() {
		return pacProntuario;
	}
	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
	public Date getPacDtNascimento() {
		return pacDtNascimento;
	}
	public void setPacDtNascimento(Date pacDtNascimento) {
		this.pacDtNascimento = pacDtNascimento;
	}
	public String getPacSexo() {
		return pacSexo;
	}
	public void setPacSexo(String pacSexo) {
		this.pacSexo = pacSexo;
	}
	public Short getNacionalidadePac() {
		return nacionalidadePac;
	}
	public void setNacionalidadePac(Short nacionalidadePac) {
		this.nacionalidadePac = nacionalidadePac;
	}
	public Byte getIndDocPac() {
		return indDocPac;
	}
	public void setIndDocPac(Byte indDocPac) {
		this.indDocPac = indDocPac;
	}
	public String getPacRG() {
		return pacRG;
	}
	public void setPacRG(String pacRG) {
		this.pacRG = pacRG;
	}
	public String getNomeResponsavelPac() {
		return nomeResponsavelPac;
	}
	public void setNomeResponsavelPac(String nomeResponsavelPac) {
		this.nomeResponsavelPac = nomeResponsavelPac;
	}
	public String getPacNomeMae() {
		return pacNomeMae;
	}
	public void setPacNomeMae(String pacNomeMae) {
		this.pacNomeMae = pacNomeMae;
	}
	public String getEndLogradouroPac() {
		return endLogradouroPac;
	}
	public void setEndLogradouroPac(String endLogradouroPac) {
		this.endLogradouroPac = endLogradouroPac;
	}
	public Integer getEndNroLogradouroPac() {
		return endNroLogradouroPac;
	}
	public void setEndNroLogradouroPac(Integer endNroLogradouroPac) {
		this.endNroLogradouroPac = endNroLogradouroPac;
	}
	public String getEndCmplLogradouroPac() {
		return endCmplLogradouroPac;
	}
	public void setEndCmplLogradouroPac(String endCmplLogradouroPac) {
		this.endCmplLogradouroPac = endCmplLogradouroPac;
	}
	public String getEndBairroPac() {
		return endBairroPac;
	}
	public void setEndBairroPac(String endBairroPac) {
		this.endBairroPac = endBairroPac;
	}
	public String getPacCor() {
		return pacCor;
	}
	public void setPacCor(String pacCor) {
		this.pacCor = pacCor;
	}
	public Integer getEtniaId() {
		return etniaId;
	}
	public void setEtniaId(Integer etniaId) {
		this.etniaId = etniaId;
	}
	public Integer getCodIbgeCidadePac() {
		return codIbgeCidadePac;
	}
	public void setCodIbgeCidadePac(Integer codIbgeCidadePac) {
		this.codIbgeCidadePac = codIbgeCidadePac;
	}
	public String getEndCidadePac() {
		return endCidadePac;
	}
	public void setEndCidadePac(String endCidadePac) {
		this.endCidadePac = endCidadePac;
	}
	public String getEndUfPac() {
		return endUfPac;
	}
	public void setEndUfPac(String endUfPac) {
		this.endUfPac = endUfPac;
	}
	public Integer getEndCepPac() {
		return endCepPac;
	}
	public void setEndCepPac(Integer endCepPac) {
		this.endCepPac = endCepPac;
	}
	public Short getDddFoneResidencial() {
		return dddFoneResidencial;
	}
	public void setDddFoneResidencial(Short dddFoneResidencial) {
		this.dddFoneResidencial = dddFoneResidencial;
	}
	public Long getFoneResidencial() {
		return foneResidencial;
	}
	public void setFoneResidencial(Long foneResidencial) {
		this.foneResidencial = foneResidencial;
	}
	public Long getIphCodSusSolic() {
		return iphCodSusSolic;
	}
	public void setIphCodSusSolic(Long iphCodSusSolic) {
		this.iphCodSusSolic = iphCodSusSolic;
	}
	public String getDescricaoIphs() {
		return descricaoIphs;
	}
	public void setDescricaoIphs(String descricaoIphs) {
		this.descricaoIphs = descricaoIphs;
	}
	public Long getIphCodSusRealiz() {
		return iphCodSusRealiz;
	}
	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}
	public String getDescricaoIphr() {
		return descricaoIphr;
	}
	public void setDescricaoIphr(String descricaoIphr) {
		this.descricaoIphr = descricaoIphr;
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
	public Byte getSaidasObitoCausa() {
		return saidasObitoCausa;
	}
	public void setSaidasObitoCausa(Byte saidasObitoCausa) {
		this.saidasObitoCausa = saidasObitoCausa;
	}
	public Byte getTciCodSus() {
		return tciCodSus;
	}
	public void setTciCodSus(Byte tciCodSus) {
		this.tciCodSus = tciCodSus;
	}
	public Date getDataInternacao() {
		return dataInternacao;
	}
	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}
	public Date getDataSaida() {
		return dataSaida;
	}
	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}
	public String getMotivoCobranca() {
		return motivoCobranca;
	}
	public void setMotivoCobranca(String motivoCobranca) {
		this.motivoCobranca = motivoCobranca;
	}
	public String getExclusaoCritica() {
		return exclusaoCritica;
	}
	public void setExclusaoCritica(String exclusaoCritica) {
		this.exclusaoCritica = exclusaoCritica;
	}
	public String getDauSenha() {
		return dauSenha;
	}
	public void setDauSenha(String dauSenha) {
		this.dauSenha = dauSenha;
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
	public Integer getCthSeq() {
		return cthSeq;
	}
	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}
	public Byte getNascidosVivos() {
		return nascidosVivos;
	}
	public void setNascidosVivos(Byte nascidosVivos) {
		this.nascidosVivos = nascidosVivos;
	}
	public Byte getNascidosMortos() {
		return nascidosMortos;
	}
	public void setNascidosMortos(Byte nascidosMortos) {
		this.nascidosMortos = nascidosMortos;
	}
	public Byte getSaidasAlta() {
		return saidasAlta;
	}
	public void setSaidasAlta(Byte saidasAlta) {
		this.saidasAlta = saidasAlta;
	}
	public Byte getSaidasObito() {
		return saidasObito;
	}
	public void setSaidasObito(Byte saidasObito) {
		this.saidasObito = saidasObito;
	}
	public Byte getSaidasTransferencia() {
		return saidasTransferencia;
	}
	public void setSaidasTransferencia(Byte saidasTransferencia) {
		this.saidasTransferencia = saidasTransferencia;
	}
	public Long getNroSisPrenatal() {
		return nroSisPrenatal;
	}
	public void setNroSisPrenatal(Long nroSisPrenatal) {
		this.nroSisPrenatal = nroSisPrenatal;
	}
	public Integer getCthSeqReapresentada() {
		return cthSeqReapresentada;
	}
	public void setCthSeqReapresentada(Integer cthSeqReapresentada) {
		this.cthSeqReapresentada = cthSeqReapresentada;
	}
	public String getOrgaoLocalRecebedor() {
		return orgaoLocalRecebedor;
	}
	public void setOrgaoLocalRecebedor(String orgaoLocalRecebedor) {
		this.orgaoLocalRecebedor = orgaoLocalRecebedor;
	}
	public String getCpfDoutor() {
		return cpfDoutor;
	}
	public void setCpfDoutor(String cpfDoutor) {
		this.cpfDoutor = cpfDoutor;
	}
	public String getCnes() {
		return cnes;
	}
	public void setCnes(String cnes) {
		this.cnes = cnes;
	}
	public String getContaFormatada() {
		return contaFormatada;
	}
	public void setContaFormatada(String contaFormatada) {
		this.contaFormatada = contaFormatada;
	}
	public String getDescricaoNacionalidade() {
		return descricaoNacionalidade;
	}
	public void setDescricaoNacionalidade(String descricaoNacionalidade) {
		this.descricaoNacionalidade = descricaoNacionalidade;
	}
	public String getDescricaoEtnia() {
		return descricaoEtnia;
	}
	public void setDescricaoEtnia(String descricaoEtnia) {
		this.descricaoEtnia = descricaoEtnia;
	}
	public String getDescricaoCid() {
		return descricaoCid;
	}
	public void setDescricaoCid(String descricaoCid) {
		this.descricaoCid = descricaoCid;
	}
	public String getCaraterAtendimento() {
		return caraterAtendimento;
	}
	public void setCaraterAtendimento(String caraterAtendimento) {
		this.caraterAtendimento = caraterAtendimento;
	}
	public String getModalidade() {
		return modalidade;
	}
	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}
	public String getMotivoSaida() {
		return motivoSaida;
	}
	public void setMotivoSaida(String motivoSaida) {
		this.motivoSaida = motivoSaida;
	}
	public Short getIphPhoSeqRealiz() {
		return iphPhoSeqRealiz;
	}
	public void setIphPhoSeqRealiz(Short iphPhoSeqRealiz) {
		this.iphPhoSeqRealiz = iphPhoSeqRealiz;
	}
	public Integer getIphSeqRealiz() {
		return iphSeqRealiz;
	}
	public void setIphSeqRealiz(Integer iphSeqRealiz) {
		this.iphSeqRealiz = iphSeqRealiz;
	}
	public String getNumeroAihFormatado() {
		return numeroAihFormatado;
	}
	public void setNumeroAihFormatado(String numeroAihFormatado) {
		this.numeroAihFormatado = numeroAihFormatado;
	}
	public void setEspecialidadeFormatada(String especialidadeFormatada) {
		this.especialidadeFormatada = especialidadeFormatada;
	}
	public String getEspecialidadeFormatada() {
		return especialidadeFormatada;
	}
	public void setSexoFormatado(String sexoFormatado) {
		this.sexoFormatado = sexoFormatado;
	}
	public String getNacionalidadeFormatada() {
		return nacionalidadeFormatada;
	}
	public void setNacionalidadeFormatada(String nacionalidadeFormatada) {
		this.nacionalidadeFormatada = nacionalidadeFormatada;
	}
	public String getSexoFormatado() {
		return sexoFormatado;
	}
	public void setTipoDocFormatado(String tipoDocFormatado) {
		this.tipoDocFormatado = tipoDocFormatado;
	}
	public String getEnderecoFormatado() {
		return enderecoFormatado;
	}
	public void setEnderecoFormatado(String enderecoFormatado) {
		this.enderecoFormatado = enderecoFormatado;
	}
	public String getTipoDocFormatado() {
		return tipoDocFormatado;
	}
	public void setCorFormatada(String corFormatada) {
		this.corFormatada = corFormatada;
	}
	public String getCorFormatada() {
		return corFormatada;
	}
	public void setEtniaFormatada(String etniaFormatada) {
		this.etniaFormatada = etniaFormatada;
	}
	public String getEtniaFormatada() {
		return etniaFormatada;
	}
	public void setMunicipioFormatado(String municipioFormatado) {
		this.municipioFormatado = municipioFormatado;
	}
	public String getCepFormatado() {
		return cepFormatado;
	}
	public void setCepFormatado(String cepFormatado) {
		this.cepFormatado = cepFormatado;
	}
	public String getTelefoneFormatado() {
		return telefoneFormatado;
	}
	public void setTelefoneFormatado(String telefoneFormatado) {
		this.telefoneFormatado = telefoneFormatado;
	}
	public String getMudaProcFormatado() {
		return mudaProcFormatado;
	}
	public void setMudaProcFormatado(String mudaProcFormatado) {
		this.mudaProcFormatado = mudaProcFormatado;
	}
	public String getProcedimentoSolicitadoFormatado() {
		return procedimentoSolicitadoFormatado;
	}
	public void setProcedimentoSolicitadoFormatado(
			String procedimentoSolicitadoFormatado) {
		this.procedimentoSolicitadoFormatado = procedimentoSolicitadoFormatado;
	}
	public String getProcedimentoPrincipalFormatado() {
		return procedimentoPrincipalFormatado;
	}
	public void setProcedimentoPrincipalFormatado(
			String procedimentoPrincipalFormatado) {
		this.procedimentoPrincipalFormatado = procedimentoPrincipalFormatado;
	}
	public String getDiagnosticoPrincipalFormatado() {
		return diagnosticoPrincipalFormatado;
	}
	public void setDiagnosticoPrincipalFormatado(
			String diagnosticoPrincipalFormatado) {
		this.diagnosticoPrincipalFormatado = diagnosticoPrincipalFormatado;
	}
	public String getDataApresentacaoFormatado() {
		return dataApresentacaoFormatado;
	}
	public void setDataApresentacaoFormatado(String dataApresentacaoFormatado) {
		this.dataApresentacaoFormatado = dataApresentacaoFormatado;
	}
	public String getDocumento() {
		return documento;
	}
	public String getMunicipioFormatado() {
		return municipioFormatado;
	}	
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}
	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public String getCnsMedicoAuditor() {
		return cnsMedicoAuditor;
	}

	public void setCnsMedicoAuditor(String cnsMedicoAuditor) {
		this.cnsMedicoAuditor = cnsMedicoAuditor;
	}

	public String getCnsMedicoResponsavel() {
		return cnsMedicoResponsavel;
	}

	public void setCnsMedicoResponsavel(String cnsMedicoResponsavel) {
		this.cnsMedicoResponsavel = cnsMedicoResponsavel;
	}

	public String getCnsMedicoSolic() {
		return cnsMedicoSolic;
	}

	public void setCnsMedicoSolic(String cnsMedicoSolic) {
		this.cnsMedicoSolic = cnsMedicoSolic;
	}

	public String getCnsDoutor() {
		return cnsDoutor;
	}

	public void setCnsDoutor(String cnsDoutor) {
		this.cnsDoutor = cnsDoutor;
	}

	public enum Fields {

		NUMERO_AIH("numeroAih"), IND_SITUACAO("indSituacao"), DCI_CPE_MES(
				"dciCpeMes"), DCI_CPE_ANO("dciCpeAno"), AIH_DTHR_EMISSAO(
				"aihDthrEmissao"), ESPECIALIDADE_AIH("especialidadeAih"), DESCRICAO_ESPECIALIDADE(
				"descricaoEspecialidade"), ENFERMARIA("enfermaria"), LEITO(
				"leito"), CPF_MEDICO_AUDITOR("cpfMedicoAuditor"), CPF_MEDICO_SOLIC(
				"cpfMedicoSolic"), CPF_MEDICO_RESPONS("cpfMedicoResponsavel"), NRO_CARTAO_SAUDE(
				"nroCartaoSaude"), PAC_NOME_PACIENTE("pacNome"), PAC_PRONTUARIO(
				"pacProntuario"), PAC_DT_NASCIMENTO("pacDtNascimento"), PAC_SEXO(
				"pacSexo"), NACIONALIDADE_PAC("nacionalidadePac"), IND_DOC_PAC(
				"indDocPac"), PAC_RG("pacRG"), NOME_RESPONSAVEL_PAC(
				"nomeResponsavelPac"), PAC_NOME_MAE("pacNomeMae"), END_LOGRADOURO_PAC(
				"endLogradouroPac"), END_NRO_LOGRADOURO_PAC(
				"endNroLogradouroPac"), END_CMPL_LOGRADOURO_PAC(
				"endCmplLogradouroPac"), END_BAIRRO_PAC("endBairroPac"), PAC_COR(
				"pacCor"), ETN_ID("etniaId"), COD_IBGE_CIDADE_PAC(
				"codIbgeCidadePac"), END_CIDADE_PAC("endCidadePac"), END_UF_PAC(
				"endUfPac"), END_CEP_PAC("endCepPac"), DDD_FONE_RESIDENCIAL(
				"dddFoneResidencial"), FONE_RESIDENCIAL("foneResidencial"), IPH_COD_SUS_SOLIC(
				"iphCodSusSolic"), DESCRICAO_IPHS("descricaoIphs"), IPH_COD_SUS_REALIZ(
				"iphCodSusRealiz"), DESCRICAO_IPHR("descricaoIphr"), CID_PRIMARIO(
				"cidPrimario"), CID_SECUNDARIO("cidSecundario"), CAUSA_OBITO(
				"saidasObitoCausa"), TCI_COD_SUS("tciCodSus"), DATA_INTERNACAO(
				"dataInternacao"), DATA_SAIDA("dataSaida"), MOTIVO_COBRANCA(
				"motivoCobranca"), EXCLUSAO_CRITICA("exclusaoCritica"), DAU_SENHA(
				"dauSenha"), NUMERO_AIH_ANTERIOR("numeroAihAnterior"), NUMERO_AIH_POSTERIOR(
				"numeroAihPosterior"), CTH_SEQ("cthSeq"), NASCIDOS_VIVOS(
				"nascidosVivos"), NASCIDOS_MORTOS("nascidosMortos"), SAIDAS_ALTA(
				"saidasAlta"), SAIDAS_OBITO("saidasObito"), SAIDAS_TRANSFERENCIA(
				"saidasTransferencia"), NRO_SISPRENATAL("nroSisPrenatal"), CTH_SEQ_REAPRESENTADA(
				"cthSeqReapresentada"), IPH_PHO_SEQ_REALIZ("iphPhoSeqRealiz"),
				IPH_SEQ_REALIZ("iphSeqRealiz"), VERSAO_SISAIH("versaoSisaih");;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getVersaoSisaih() {
		return versaoSisaih;
	}

	public void setVersaoSisaih(String versaoSisaih) {
		this.versaoSisaih = versaoSisaih;
	}
}

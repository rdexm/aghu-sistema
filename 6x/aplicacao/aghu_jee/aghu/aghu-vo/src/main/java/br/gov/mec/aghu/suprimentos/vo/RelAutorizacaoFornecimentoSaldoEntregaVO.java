/**
 * 
 */
package br.gov.mec.aghu.suprimentos.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class RelAutorizacaoFornecimentoSaldoEntregaVO implements Serializable {

	private static final long serialVersionUID = -2976052401815804103L;

	private Integer afnNumero;					//	afn_numero
	private String razaoSocHospital;			//	razao_soc_hospital
	private String titulo;						//	titulo
	private String enderecoHospital;			//	endereco_hospital
	private String cepHospital;					//	cep_hospital
	private String numeroAf;					//	numero_af
	private String labelModal;					//03	label_modal
	private Short dataPrevEntrega;				//13	data_prev_entrega
	private Long numCgcHospital;				//	num_cgc_hospital
	private Date dataGeracao;					//	data_geracao
	private String cidadeHospital;				//	cidade_hospital
	private String foneHospital;				//	fone_hospital
	private String faxHospital;					//	fax_hospital
	private Date dataAlteracao;					//	data_alteracao
	private Date venctoContrato;				//11	vencto_contrato
	private Integer seqAlteracao;				//	seq_alteracao
	private String descAlteracao;				//	desc_alteracao
	private String razaoSocFornec;				//12	razao_soc_fornec
	private String observacao;					//10	observacao
	private Long numCgcFornec;					//14	num_cgc_fornec
	private String enderecoFornec;				//16	endereco_fornec
	private String cidadeFornec;				//17	cidade_fornec
	private String foneFornec;					//19	fone_fornec
	private String faxFornec;					//20	fax_fornec
	private String contaNro;					//15	conta_nro
	private Integer agencNro;					//18	agenc_nro
	private Integer bancoNro;					//21	banco_nro
	private Integer labelConvenio;				//	label_convenio
	private String convenio;					//07	convenio
	private String numEmpenho;					//04	num_empenho
	private String anoListaItens;				//06	ano_lista_itens
	private Double vlrEmpenho;					//	vlr_empenho			** Função na primeria query  **
	private Integer modEmpenho;					//08	mod_empenho
	private String natDespesa;					//09	nat_despesa
	private String inciso;						//	inciso
	private String artigo;						//	artigo
	private Short item;							//22	item
	private String nome;						//23	nome
	private String descricao;					//29	descricao
	private Integer qtAutoriz;					//	qt_autoriz
	private Integer codigo;						//24	codigo
	private String un;							//25	un
	private Integer qtReforco;					//26	qt_reforco
	private Integer qtRcbd;						//	qt_rcbd
	private Integer qtSldo;						//	qt_sldo
	private Boolean orig;						//	orig
	private Double vlrUnitario;					//27	vlr_unitario
	private Double vlrItem;						//28	vlr_item
	private Double vlrEfetivado;				//	vlr_efetivado
	private Double perDescontoItem;				//	per_desconto_item
	private Double perDesconto;					//	per_desconto
	private Double perAcrescimoItem;			//	per_acrescimo_item
	private Double perAcrescimo;				//	per_acrescimo
	private Double perIpi;						//	per_ipi
	private Integer mcmCodigo;					//	mcm_codigo
	private Integer ncMcmCodigo;				//	nc_mcm_codigo
	private Integer ncNumero;					//	nc_numero
	private Integer afjnNumero;					//	afjn_numero
	private Integer slsNumero;					//	sls_numero
	private String apresentacao;				//	apresentacao
	private Integer nroProjeto;					//	nro_projeto
	private DominioTipoFaseSolicitacao tipo;	//	tipo
	private String nroOrcamento;				//31	nro_orcamento
	private String numeroAfSeq;					//48
	
	private Date dataAtual;						//05
	private String marcaNomeComer;				//30
	private Double valorAfOriginal;				//32
	private String descFormaPgto;				//33
	private String numParcelas;					//34
	private String comprador;					//35
	private String ramal;						//36
	private String parcelas;					//37
	private Integer prazoEntrega;				//38
	private String chefeServico;				//39
	private String coordenador;					//40
	private String razaoSocial;					//41
	private String cnpj;						//42
	private String localEntregaAf;				//43
	private String enderecoEntregaAf;			//44
	private String bairroEntregaAf;				//45
	private String cepEntregaAf;				//46
	private String horarioEntregaAf;			//47
	
	//Parametros da busca
	private Integer pfrLctNumero;
	private Integer sequenciaAlteracao;
	private Short nroComplemento;
	
	public enum Fields{
		AFN_NUMERO("afnNumero"),		
		RAZAO_SOCIAL_HOSPITAL("razaoSocHospital"),
		TITULO("titulo"),
		ENDERECO_HOSPITAL("enderecoHospital"),
		CEP_HOSPITAL("cepHospital"),
		NUMERO_AF("numeroAf"),
		LABEL_MODAL("labelModal"),
		DATA_PREV_ENTREGA("dataPrevEntrega"),
		NUM_CGC_HOSPITAL("numCgcHospital"),
		DATA_GERACAO("dataGeracao"),
		CIDADE_HOSPITAL("cidadeHospital"),
		FONE_HOSPITAL("foneHospital"),
		FAX_HOSPITAL("faxHospital"),
		DATA_ALTERACAO("dataAlteracao"),
		VENCTO_CONTRATO("venctoContrato"),
		SEQ_ALTERACAO("seqAlteracao"),
		DES_ALTERACAO("descAlteracao"),
		RAZAO_SOC_FORNEC("razaoSocFornec"),
		OBSERVACAO("observacao"),
		NUM_CGC_FORNEC("numCgcFornec"),
		ENDERECO_FORNEC("enderecoFornec"),
		CIDADE_FORNEC("cidadeFornec"),
		FONE_FORNEC("foneFornec"),
		FAX_FORNEC("faxFornec"),
		CONTA_NRO("contaNro"),
		AGENC_NRO("agencNro"),
		BANCO_NRO("bancoNro"),
		LABEL_CONVENIO("labelConvenio"),
		CONVENIO("convenio"),
		NUM_EMPENHO("numEmpenho"),
		ANO_LISTA_ITENS("anoListaItens"),
		VLR_EMPENHO("vlrEmpenho"),
		MOD_EMPENHO("modEmpenho"),
		NAT_DESPESA("natDespesa"),
		INCISO("inciso"),
		ARTIGO("artigo"),
		ITEM("item"),
		NOME("nome"),
		DESCRICAO("descricao"),
		QT_AUTORIZ("qtAutoriz"),
		CODIGO("codigo"),
		UN("un"),
		QT_REFORCO("qtReforco"),
		QT_RCBD("qtRcbd"),
		QT_SLDO("qtSldo"),
		ORIG("orig"),
		VLR_UNITARIO("vlrUnitario"),
		VLR_ITEM("vlrItem"),
		VLR_EFETIVADO("vlrEfetivado"),
		PER_DESCONTO_ITEM("perDescontoItem"),
		PER_DESCONTO("perDesconto"),
		PER_ACRESCIMO_ITEM("perAcrescimoItem"),
		PER_ACRESCIMO("perAcrescimo"),
		PER_IPI("perIpi"),
		MCM_CODIGO("mcmCodigo"),
		NC_MCM_CODIGO("ncMcmCodigo"),
		NC_NUMERO("ncNumero"),
		AFJN_NUMERO("afjnNumero"),
		SLS_NUMERO("slsNumero"),
		APRESENTACAO("apresentacao"),
		NRO_PROJETO("nroProjeto"),
		TIPO("tipo"),
		NRO_ORCAMENTO("nroOrcamento"),
		DATA_ATUAL("dataAtual"),
		MARCA_NOME_COMER("marcaNomeComer"),
		VALOR_AF_ORIGINAL("valorAfOriginal"),
		DESC_FORMA_PGTO("descFormaPgto"),
		NUM_PARCELAS("numParcelas"),
		COMPRADOR("comprador"),
		RAMAL("ramal"),
		PARCELAS("parcelas"),
		PRAZO_ENTREGA("prazoEntrega"),
		CHEFE_SERVICO("chefeServico"),
		COORDENADOR("coordenador"),
		RAZAO_SOCIAL("razaoSocial"),
		CNPJ("cnpj"),
		LOCAL_ENTREGA_AF("localEntregaAf"),
		ENDERECO_ENTREGA_AF("enderecoEntregaAf"),
		BAIRRO_ENTREGA_AF("bairroEntregaAf"),
		CEP_ENTREGA_AF("cepEntregaAf"),
		HORARIO_ENTREGA_AF("horarioEntregaAf");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public String getRazaoSocHospital() {
		return razaoSocHospital;
	}

	public void setRazaoSocHospital(String razaoSocHospital) {
		this.razaoSocHospital = razaoSocHospital;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getEnderecoHospital() {
		return enderecoHospital;
	}

	public void setEnderecoHospital(String enderecoHospital) {
		this.enderecoHospital = enderecoHospital;
	}

	public String getCepHospital() {
		return cepHospital;
	}

	public void setCepHospital(String cepHospital) {
		this.cepHospital = cepHospital;
	}

	public String getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(String numeroAf) {
		this.numeroAf = numeroAf;
	}

	public String getLabelModal() {
		return labelModal;
	}

	public void setLabelModal(String labelModal) {
		this.labelModal = labelModal;
	}

	public Short getDataPrevEntrega() {
		return dataPrevEntrega;
	}

	public void setDataPrevEntrega(Short dataPrevEntrega) {
		this.dataPrevEntrega = dataPrevEntrega;
	}

	public Long getNumCgcHospital() {
		return numCgcHospital;
	}

	public void setNumCgcHospital(Long numCgcHospital) {
		this.numCgcHospital = numCgcHospital;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public String getCidadeHospital() {
		return cidadeHospital;
	}

	public void setCidadeHospital(String cidadeHospital) {
		this.cidadeHospital = cidadeHospital;
	}

	public String getFoneHospital() {
		return foneHospital;
	}

	public void setFoneHospital(String foneHospital) {
		this.foneHospital = foneHospital;
	}

	public String getFaxHospital() {
		return faxHospital;
	}

	public void setFaxHospital(String faxHospital) {
		this.faxHospital = faxHospital;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public Date getVenctoContrato() {
		return venctoContrato;
	}

	public void setVenctoContrato(Date venctoContrato) {
		this.venctoContrato = venctoContrato;
	}

	public Integer getSeqAlteracao() {
		return seqAlteracao;
	}

	public void setSeqAlteracao(Integer seqAlteracao) {
		this.seqAlteracao = seqAlteracao;
	}

	public String getDescAlteracao() {
		return descAlteracao;
	}

	public void setDescAlteracao(String descAlteracao) {
		this.descAlteracao = descAlteracao;
	}

	public String getRazaoSocFornec() {
		return razaoSocFornec;
	}

	public void setRazaoSocFornec(String razaoSocFornec) {
		this.razaoSocFornec = razaoSocFornec;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Long getNumCgcFornec() {
		return numCgcFornec;
	}

	public void setNumCgcFornec(Long numCgcFornec) {
		this.numCgcFornec = numCgcFornec;
	}

	public String getEnderecoFornec() {
		return enderecoFornec;
	}

	public void setEnderecoFornec(String enderecoFornec) {
		this.enderecoFornec = enderecoFornec;
	}

	public String getCidadeFornec() {
		return cidadeFornec;
	}

	public void setCidadeFornec(String cidadeFornec) {
		this.cidadeFornec = cidadeFornec;
	}

	public String getFoneFornec() {
		return foneFornec;
	}

	public void setFoneFornec(String foneFornec) {
		this.foneFornec = foneFornec;
	}

	public String getFaxFornec() {
		return faxFornec;
	}

	public void setFaxFornec(String faxFornec) {
		this.faxFornec = faxFornec;
	}

	public String getContaNro() {
		return contaNro;
	}

	public void setContaNro(String contaNro) {
		this.contaNro = contaNro;
	}

	public Integer getAgencNro() {
		return agencNro;
	}

	public void setAgencNro(Integer agencNro) {
		this.agencNro = agencNro;
	}

	public Integer getBancoNro() {
		return bancoNro;
	}

	public void setBancoNro(Integer bancoNro) {
		this.bancoNro = bancoNro;
	}

	public Integer getLabelConvenio() {
		return labelConvenio;
	}

	public void setLabelConvenio(Integer labelConvenio) {
		this.labelConvenio = labelConvenio;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getNumEmpenho() {
		return numEmpenho;
	}

	public void setNumEmpenho(String numEmpenho) {
		this.numEmpenho = numEmpenho;
	}

	public String getAnoListaItens() {
		return anoListaItens;
	}

	public void setAnoListaItens(String anoListaItens) {
		this.anoListaItens = anoListaItens;
	}

	public Double getVlrEmpenho() {
		return vlrEmpenho;
	}

	public void setVlrEmpenho(Double vlrEmpenho) {
		this.vlrEmpenho = vlrEmpenho;
	}

	public Integer getModEmpenho() {
		return modEmpenho;
	}

	public void setModEmpenho(Integer modEmpenho) {
		this.modEmpenho = modEmpenho;
	}

	public String getNatDespesa() {
		return natDespesa;
	}

	public void setNatDespesa(String natDespesa) {
		this.natDespesa = natDespesa;
	}

	public String getInciso() {
		return inciso;
	}

	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	public String getArtigo() {
		return artigo;
	}

	public void setArtigo(String artigo) {
		this.artigo = artigo;
	}

	public Short getItem() {
		return item;
	}

	public void setItem(Short item) {
		this.item = item;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getQtAutoriz() {
		return qtAutoriz;
	}

	public void setQtAutoriz(Integer qtAutoriz) {
		this.qtAutoriz = qtAutoriz;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getUn() {
		return un;
	}

	public void setUn(String un) {
		this.un = un;
	}

	public Integer getQtReforco() {
		return qtReforco;
	}

	public void setQtReforco(Integer qtReforco) {
		this.qtReforco = qtReforco;
	}

	public Integer getQtRcbd() {
		return qtRcbd;
	}

	public void setQtRcbd(Integer qtRcbd) {
		this.qtRcbd = qtRcbd;
	}

	public Integer getQtSldo() {
		return qtSldo;
	}

	public void setQtSldo(Integer qtSldo) {
		this.qtSldo = qtSldo;
	}

	public Boolean getOrig() {
		return orig;
	}

	public void setOrig(Boolean orig) {
		this.orig = orig;
	}

	public Double getVlrUnitario() {
		return vlrUnitario;
	}

	public void setVlrUnitario(Double vlrUnitario) {
		this.vlrUnitario = vlrUnitario;
	}

	public Double getVlrItem() {
		return vlrItem;
	}

	public void setVlrItem(Double vlrItem) {
		this.vlrItem = vlrItem;
	}

	public Double getVlrEfetivado() {
		return vlrEfetivado;
	}

	public void setVlrEfetivado(Double vlrEfetivado) {
		this.vlrEfetivado = vlrEfetivado;
	}

	public Double getPerDescontoItem() {
		return perDescontoItem;
	}

	public void setPerDescontoItem(Double perDescontoItem) {
		this.perDescontoItem = perDescontoItem;
	}

	public Double getPerDesconto() {
		return perDesconto;
	}

	public void setPerDesconto(Double perDesconto) {
		this.perDesconto = perDesconto;
	}

	public Double getPerAcrescimoItem() {
		return perAcrescimoItem;
	}

	public void setPerAcrescimoItem(Double perAcrescimoItem) {
		this.perAcrescimoItem = perAcrescimoItem;
	}

	public Double getPerAcrescimo() {
		return perAcrescimo;
	}

	public void setPerAcrescimo(Double perAcrescimo) {
		this.perAcrescimo = perAcrescimo;
	}

	public Double getPerIpi() {
		return perIpi;
	}

	public void setPerIpi(Double perIpi) {
		this.perIpi = perIpi;
	}

	public Integer getMcmCodigo() {
		return mcmCodigo;
	}

	public void setMcmCodigo(Integer mcmCodigo) {
		this.mcmCodigo = mcmCodigo;
	}

	public Integer getNcMcmCodigo() {
		return ncMcmCodigo;
	}

	public void setNcMcmCodigo(Integer ncMcmCodigo) {
		this.ncMcmCodigo = ncMcmCodigo;
	}

	public Integer getNcNumero() {
		return ncNumero;
	}

	public void setNcNumero(Integer ncNumero) {
		this.ncNumero = ncNumero;
	}

	public Integer getAfjnNumero() {
		return afjnNumero;
	}

	public void setAfjnNumero(Integer afjnNumero) {
		this.afjnNumero = afjnNumero;
	}

	public Integer getSlsNumero() {
		return slsNumero;
	}

	public void setSlsNumero(Integer slsNumero) {
		this.slsNumero = slsNumero;
	}

	public String getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	public Integer getNroProjeto() {
		return nroProjeto;
	}

	public void setNroProjeto(Integer nroProjeto) {
		this.nroProjeto = nroProjeto;
	}

	public DominioTipoFaseSolicitacao getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoFaseSolicitacao tipo) {
		this.tipo = tipo;
	}

	public String getNroOrcamento() {
		return nroOrcamento;
	}

	public void setNroOrcamento(String nroOrcamento) {
		this.nroOrcamento = nroOrcamento;
	}

	public Date getDataAtual() {
		return dataAtual;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	public String getMarcaNomeComer() {
		return marcaNomeComer;
	}

	public void setMarcaNomeComer(String marcaNomeComer) {
		this.marcaNomeComer = marcaNomeComer;
	}

	public Double getValorAfOriginal() {
		return valorAfOriginal;
	}

	public void setValorAfOriginal(Double valorAfOriginal) {
		this.valorAfOriginal = valorAfOriginal;
	}

	public String getDescFormaPgto() {
		return descFormaPgto;
	}

	public void setDescFormaPgto(String descFormaPgto) {
		this.descFormaPgto = descFormaPgto;
	}

	public String getNumParcelas() {
		return numParcelas;
	}

	public void setNumParcelas(String numParcelas) {
		this.numParcelas = numParcelas;
	}

	public String getComprador() {
		return comprador;
	}

	public void setComprador(String comprador) {
		this.comprador = comprador;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public String getParcelas() {
		return parcelas;
	}

	public void setParcelas(String parcelas) {
		this.parcelas = parcelas;
	}

	public Integer getPrazoEntrega() {
		return prazoEntrega;
	}

	public void setPrazoEntrega(Integer prazoEntrega) {
		this.prazoEntrega = prazoEntrega;
	}

	public String getChefeServico() {
		return chefeServico;
	}

	public void setChefeServico(String chefeServico) {
		this.chefeServico = chefeServico;
	}

	public String getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(String coordenador) {
		this.coordenador = coordenador;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getLocalEntregaAf() {
		return localEntregaAf;
	}

	public void setLocalEntregaAf(String localEntregaAf) {
		this.localEntregaAf = localEntregaAf;
	}

	public String getEnderecoEntregaAf() {
		return enderecoEntregaAf;
	}

	public void setEnderecoEntregaAf(String enderecoEntregaAf) {
		this.enderecoEntregaAf = enderecoEntregaAf;
	}

	public String getBairroEntregaAf() {
		return bairroEntregaAf;
	}

	public void setBairroEntregaAf(String bairroEntregaAf) {
		this.bairroEntregaAf = bairroEntregaAf;
	}

	public String getCepEntregaAf() {
		return cepEntregaAf;
	}

	public void setCepEntregaAf(String cepEntregaAf) {
		this.cepEntregaAf = cepEntregaAf;
	}

	public String getHorarioEntregaAf() {
		return horarioEntregaAf;
	}

	public void setHorarioEntregaAf(String horarioEntregaAf) {
		this.horarioEntregaAf = horarioEntregaAf;
	}

	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}

	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	public Integer getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Integer sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public void setNumeroAfSeq(String numeroAfSeq) {
		this.numeroAfSeq = numeroAfSeq;
	}

	public String getNumeroAfSeq() {
		return numeroAfSeq;
	}

	@Override
	public int hashCode() {
		return afjnNumero;
	}

	@Override
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RelAutorizacaoFornecimentoSaldoEntregaVO other = (RelAutorizacaoFornecimentoSaldoEntregaVO) obj;
		if (afjnNumero == null) {
			if (other.afjnNumero != null) {
				return false;
			}
		} else if (!afjnNumero.equals(other.afjnNumero)) {
			return false;
		}
		if (afnNumero == null) {
			if (other.afnNumero != null) {
				return false;
			}
		} else if (!afnNumero.equals(other.afnNumero)) {
			return false;
		}
		if (agencNro == null) {
			if (other.agencNro != null) {
				return false;
			}
		} else if (!agencNro.equals(other.agencNro)) {
			return false;
		}
		if (anoListaItens == null) {
			if (other.anoListaItens != null) {
				return false;
			}
		} else if (!anoListaItens.equals(other.anoListaItens)) {
			return false;
		}
		if (apresentacao == null) {
			if (other.apresentacao != null) {
				return false;
			}
		} else if (!apresentacao.equals(other.apresentacao)) {
			return false;
		}
		if (artigo == null) {
			if (other.artigo != null) {
				return false;
			}
		} else if (!artigo.equals(other.artigo)) {
			return false;
		}
		if (bairroEntregaAf == null) {
			if (other.bairroEntregaAf != null) {
				return false;
			}
		} else if (!bairroEntregaAf.equals(other.bairroEntregaAf)) {
			return false;
		}
		if (bancoNro == null) {
			if (other.bancoNro != null) {
				return false;
			}
		} else if (!bancoNro.equals(other.bancoNro)) {
			return false;
		}
		if (cepEntregaAf == null) {
			if (other.cepEntregaAf != null) {
				return false;
			}
		} else if (!cepEntregaAf.equals(other.cepEntregaAf)) {
			return false;
		}
		if (cepHospital == null) {
			if (other.cepHospital != null) {
				return false;
			}
		} else if (!cepHospital.equals(other.cepHospital)) {
			return false;
		}
		if (chefeServico == null) {
			if (other.chefeServico != null) {
				return false;
			}
		} else if (!chefeServico.equals(other.chefeServico)) {
			return false;
		}
		if (cidadeFornec == null) {
			if (other.cidadeFornec != null) {
				return false;
			}
		} else if (!cidadeFornec.equals(other.cidadeFornec)) {
			return false;
		}
		if (cidadeHospital == null) {
			if (other.cidadeHospital != null) {
				return false;
			}
		} else if (!cidadeHospital.equals(other.cidadeHospital)) {
			return false;
		}
		if (cnpj == null) {
			if (other.cnpj != null) {
				return false;
			}
		} else if (!cnpj.equals(other.cnpj)) {
			return false;
		}
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		if (comprador == null) {
			if (other.comprador != null) {
				return false;
			}
		} else if (!comprador.equals(other.comprador)) {
			return false;
		}
		if (contaNro == null) {
			if (other.contaNro != null) {
				return false;
			}
		} else if (!contaNro.equals(other.contaNro)) {
			return false;
		}
		if (convenio == null) {
			if (other.convenio != null) {
				return false;
			}
		} else if (!convenio.equals(other.convenio)) {
			return false;
		}
		if (coordenador == null) {
			if (other.coordenador != null) {
				return false;
			}
		} else if (!coordenador.equals(other.coordenador)) {
			return false;
		}
		if (dataAlteracao == null) {
			if (other.dataAlteracao != null) {
				return false;
			}
		} else if (!dataAlteracao.equals(other.dataAlteracao)) {
			return false;
		}
		if (dataAtual == null) {
			if (other.dataAtual != null) {
				return false;
			}
		} else if (!dataAtual.equals(other.dataAtual)) {
			return false;
		}
		if (dataGeracao == null) {
			if (other.dataGeracao != null) {
				return false;
			}
		} else if (!dataGeracao.equals(other.dataGeracao)) {
			return false;
		}
		if (dataPrevEntrega == null) {
			if (other.dataPrevEntrega != null) {
				return false;
			}
		} else if (!dataPrevEntrega.equals(other.dataPrevEntrega)) {
			return false;
		}
		if (descAlteracao == null) {
			if (other.descAlteracao != null) {
				return false;
			}
		} else if (!descAlteracao.equals(other.descAlteracao)) {
			return false;
		}
		if (descFormaPgto == null) {
			if (other.descFormaPgto != null) {
				return false;
			}
		} else if (!descFormaPgto.equals(other.descFormaPgto)) {
			return false;
		}
		if (descricao == null) {
			if (other.descricao != null) {
				return false;
			}
		} else if (!descricao.equals(other.descricao)) {
			return false;
		}
		if (enderecoEntregaAf == null) {
			if (other.enderecoEntregaAf != null) {
				return false;
			}
		} else if (!enderecoEntregaAf.equals(other.enderecoEntregaAf)) {
			return false;
		}
		if (enderecoFornec == null) {
			if (other.enderecoFornec != null) {
				return false;
			}
		} else if (!enderecoFornec.equals(other.enderecoFornec)) {
			return false;
		}
		if (enderecoHospital == null) {
			if (other.enderecoHospital != null) {
				return false;
			}
		} else if (!enderecoHospital.equals(other.enderecoHospital)) {
			return false;
		}
		if (faxFornec == null) {
			if (other.faxFornec != null) {
				return false;
			}
		} else if (!faxFornec.equals(other.faxFornec)) {
			return false;
		}
		if (faxHospital == null) {
			if (other.faxHospital != null) {
				return false;
			}
		} else if (!faxHospital.equals(other.faxHospital)) {
			return false;
		}
		if (foneFornec == null) {
			if (other.foneFornec != null) {
				return false;
			}
		} else if (!foneFornec.equals(other.foneFornec)) {
			return false;
		}
		if (foneHospital == null) {
			if (other.foneHospital != null) {
				return false;
			}
		} else if (!foneHospital.equals(other.foneHospital)) {
			return false;
		}
		if (horarioEntregaAf == null) {
			if (other.horarioEntregaAf != null) {
				return false;
			}
		} else if (!horarioEntregaAf.equals(other.horarioEntregaAf)) {
			return false;
		}
		if (inciso == null) {
			if (other.inciso != null) {
				return false;
			}
		} else if (!inciso.equals(other.inciso)) {
			return false;
		}
		if (item == null) {
			if (other.item != null) {
				return false;
			}
		} else if (!item.equals(other.item)) {
			return false;
		}
		if (labelConvenio == null) {
			if (other.labelConvenio != null) {
				return false;
			}
		} else if (!labelConvenio.equals(other.labelConvenio)) {
			return false;
		}
		if (labelModal == null) {
			if (other.labelModal != null) {
				return false;
			}
		} else if (!labelModal.equals(other.labelModal)) {
			return false;
		}
		if (localEntregaAf == null) {
			if (other.localEntregaAf != null) {
				return false;
			}
		} else if (!localEntregaAf.equals(other.localEntregaAf)) {
			return false;
		}
		if (marcaNomeComer == null) {
			if (other.marcaNomeComer != null) {
				return false;
			}
		} else if (!marcaNomeComer.equals(other.marcaNomeComer)) {
			return false;
		}
		if (mcmCodigo == null) {
			if (other.mcmCodigo != null) {
				return false;
			}
		} else if (!mcmCodigo.equals(other.mcmCodigo)) {
			return false;
		}
		if (modEmpenho == null) {
			if (other.modEmpenho != null) {
				return false;
			}
		} else if (!modEmpenho.equals(other.modEmpenho)) {
			return false;
		}
		if (natDespesa == null) {
			if (other.natDespesa != null) {
				return false;
			}
		} else if (!natDespesa.equals(other.natDespesa)) {
			return false;
		}
		if (ncMcmCodigo == null) {
			if (other.ncMcmCodigo != null) {
				return false;
			}
		} else if (!ncMcmCodigo.equals(other.ncMcmCodigo)) {
			return false;
		}
		if (ncNumero == null) {
			if (other.ncNumero != null) {
				return false;
			}
		} else if (!ncNumero.equals(other.ncNumero)) {
			return false;
		}
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		if (nroComplemento == null) {
			if (other.nroComplemento != null) {
				return false;
			}
		} else if (!nroComplemento.equals(other.nroComplemento)) {
			return false;
		}
		if (nroOrcamento == null) {
			if (other.nroOrcamento != null) {
				return false;
			}
		} else if (!nroOrcamento.equals(other.nroOrcamento)) {
			return false;
		}
		if (nroProjeto == null) {
			if (other.nroProjeto != null) {
				return false;
			}
		} else if (!nroProjeto.equals(other.nroProjeto)) {
			return false;
		}
		if (numCgcFornec == null) {
			if (other.numCgcFornec != null) {
				return false;
			}
		} else if (!numCgcFornec.equals(other.numCgcFornec)) {
			return false;
		}
		if (numCgcHospital == null) {
			if (other.numCgcHospital != null) {
				return false;
			}
		} else if (!numCgcHospital.equals(other.numCgcHospital)) {
			return false;
		}
		if (numEmpenho == null) {
			if (other.numEmpenho != null) {
				return false;
			}
		} else if (!numEmpenho.equals(other.numEmpenho)) {
			return false;
		}
		if (numParcelas == null) {
			if (other.numParcelas != null) {
				return false;
			}
		} else if (!numParcelas.equals(other.numParcelas)) {
			return false;
		}
		if (numeroAf == null) {
			if (other.numeroAf != null) {
				return false;
			}
		} else if (!numeroAf.equals(other.numeroAf)) {
			return false;
		}
		if (numeroAfSeq == null) {
			if (other.numeroAfSeq != null) {
				return false;
			}
		} else if (!numeroAfSeq.equals(other.numeroAfSeq)) {
			return false;
		}
		if (observacao == null) {
			if (other.observacao != null) {
				return false;
			}
		} else if (!observacao.equals(other.observacao)) {
			return false;
		}
		if (orig == null) {
			if (other.orig != null) {
				return false;
			}
		} else if (!orig.equals(other.orig)) {
			return false;
		}
		if (parcelas == null) {
			if (other.parcelas != null) {
				return false;
			}
		} else if (!parcelas.equals(other.parcelas)) {
			return false;
		}
		if (perAcrescimo == null) {
			if (other.perAcrescimo != null) {
				return false;
			}
		} else if (!perAcrescimo.equals(other.perAcrescimo)) {
			return false;
		}
		if (perAcrescimoItem == null) {
			if (other.perAcrescimoItem != null) {
				return false;
			}
		} else if (!perAcrescimoItem.equals(other.perAcrescimoItem)) {
			return false;
		}
		if (perDesconto == null) {
			if (other.perDesconto != null) {
				return false;
			}
		} else if (!perDesconto.equals(other.perDesconto)) {
			return false;
		}
		if (perDescontoItem == null) {
			if (other.perDescontoItem != null) {
				return false;
			}
		} else if (!perDescontoItem.equals(other.perDescontoItem)) {
			return false;
		}
		if (perIpi == null) {
			if (other.perIpi != null) {
				return false;
			}
		} else if (!perIpi.equals(other.perIpi)) {
			return false;
		}
		if (pfrLctNumero == null) {
			if (other.pfrLctNumero != null) {
				return false;
			}
		} else if (!pfrLctNumero.equals(other.pfrLctNumero)) {
			return false;
		}
		if (prazoEntrega == null) {
			if (other.prazoEntrega != null) {
				return false;
			}
		} else if (!prazoEntrega.equals(other.prazoEntrega)) {
			return false;
		}
		if (qtAutoriz == null) {
			if (other.qtAutoriz != null) {
				return false;
			}
		} else if (!qtAutoriz.equals(other.qtAutoriz)) {
			return false;
		}
		if (qtRcbd == null) {
			if (other.qtRcbd != null) {
				return false;
			}
		} else if (!qtRcbd.equals(other.qtRcbd)) {
			return false;
		}
		if (qtReforco == null) {
			if (other.qtReforco != null) {
				return false;
			}
		} else if (!qtReforco.equals(other.qtReforco)) {
			return false;
		}
		if (qtSldo == null) {
			if (other.qtSldo != null) {
				return false;
			}
		} else if (!qtSldo.equals(other.qtSldo)) {
			return false;
		}
		if (ramal == null) {
			if (other.ramal != null) {
				return false;
			}
		} else if (!ramal.equals(other.ramal)) {
			return false;
		}
		if (razaoSocFornec == null) {
			if (other.razaoSocFornec != null) {
				return false;
			}
		} else if (!razaoSocFornec.equals(other.razaoSocFornec)) {
			return false;
		}
		if (razaoSocHospital == null) {
			if (other.razaoSocHospital != null) {
				return false;
			}
		} else if (!razaoSocHospital.equals(other.razaoSocHospital)) {
			return false;
		}
		if (razaoSocial == null) {
			if (other.razaoSocial != null) {
				return false;
			}
		} else if (!razaoSocial.equals(other.razaoSocial)) {
			return false;
		}
		if (seqAlteracao == null) {
			if (other.seqAlteracao != null) {
				return false;
			}
		} else if (!seqAlteracao.equals(other.seqAlteracao)) {
			return false;
		}
		if (sequenciaAlteracao == null) {
			if (other.sequenciaAlteracao != null) {
				return false;
			}
		} else if (!sequenciaAlteracao.equals(other.sequenciaAlteracao)) {
			return false;
		}
		if (slsNumero == null) {
			if (other.slsNumero != null) {
				return false;
			}
		} else if (!slsNumero.equals(other.slsNumero)) {
			return false;
		}
		if (tipo != other.tipo) {
			return false;
		}
		if (titulo == null) {
			if (other.titulo != null) {
				return false;
			}
		} else if (!titulo.equals(other.titulo)) {
			return false;
		}
		if (un == null) {
			if (other.un != null) {
				return false;
			}
		} else if (!un.equals(other.un)) {
			return false;
		}
		if (valorAfOriginal == null) {
			if (other.valorAfOriginal != null) {
				return false;
			}
		} else if (!valorAfOriginal.equals(other.valorAfOriginal)) {
			return false;
		}
		if (venctoContrato == null) {
			if (other.venctoContrato != null) {
				return false;
			}
		} else if (!venctoContrato.equals(other.venctoContrato)) {
			return false;
		}
		if (vlrEfetivado == null) {
			if (other.vlrEfetivado != null) {
				return false;
			}
		} else if (!vlrEfetivado.equals(other.vlrEfetivado)) {
			return false;
		}
		if (vlrEmpenho == null) {
			if (other.vlrEmpenho != null) {
				return false;
			}
		} else if (!vlrEmpenho.equals(other.vlrEmpenho)) {
			return false;
		}
		if (vlrItem == null) {
			if (other.vlrItem != null) {
				return false;
			}
		} else if (!vlrItem.equals(other.vlrItem)) {
			return false;
		}
		if (vlrUnitario == null) {
			if (other.vlrUnitario != null) {
				return false;
			}
		} else if (!vlrUnitario.equals(other.vlrUnitario)) {
			return false;
		}
		return true;
	}
}

package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ProgrGeralEntregaAFVO implements BaseBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3880908728556082249L;

	private static final String HIFEN_ESPACADO = " - ";

	//Para interface
	private Integer seq;
	
	//Sco_Progr_Entrega_Itens_Af.Seq
	private Integer progEntregaItemAFSeq;
	
	//Sco_Autorizacoes_Forn.Numero
	private Integer nroAF;
	
	//Sco_Autorizacoes_Forn.Pfr_Lct_Numero
	private Integer af;
	
	//Sco_Autorizacoes_Forn.Nro_Complemento
	private Short cp;
	
	//Sco_Progr_Entrega_Itens_Af.Afe_Numero
	private Integer afp;
	
	//Sco_Itens_Autorizacao_Forn.Numero
	private Integer itemAF;
	
	//Sco_Progr_Entrega_Itens_Af.parcela
	private Integer parcela;
	
	//Sco_Progr_Entrega_Itens_Af.Dt_Prev_Entrega
	private Date previsaoDt;
	
	//Sco_Progr_Entrega_Itens_Af.Qtde
	private Integer qtdParcela;
	
	//Sco_Itens_Autorizacao_Forn.Umd_Codigo	
	private String umdForn;
	
	//Sco_Itens_Autorizacao_Forn.Fator_Conversao
	private Integer fatorConv;
	
	//Sco_Materiais.Umd_Codigo
	private String umdMat;
	
	// Sco_Progr_Entrega_Itens_Af.Qtde * Sco_Itens_Autorizacao_Forn.Fator_Conversao
	private Double qtdReceber;
	
	//Sco_Materiais.Alm_Seq
	private Short alm;
	
	//Sce_almoxarifados.descr
	private String descAlm;
	
	//RN1
	private Integer espacoDisponivel;
	
	//sco_grupos_materiais.codigo
	private Integer codGrupoMat;
	
	//sco_grupos_materiais.descricao
	private String descGrupoMat;
	
	//C03
	private DominioClassifABC dominioABC;
	
	private Integer matCodigo;
	
	private String matNome;
	
	//Sco_Materiais.Codigo + ' - ' + Sco_Materiais.Nome  
	private String matServ;
	
	private boolean isServico;
	
	//Sco_Fornecedores.Numero
	private Integer numeroFornecedor;
	
	//Sco_Fornecedores.cnpj
	private String cnpjFornecedor;
	
	//Sco_Fornecedores.cpf
	private Long cpfFornecedor;
	
	//Sco_Fornecedores.cgc
	private Long cgcFornecedor;
	
	//Sco_Fornecedores.Razao_Social
	private String razaoSocialFornecedor;
	
	//Sco_Progr_Entrega_Itens_Af.ind_empenhada
	private String emp;
	private String empChecked;
	private DominioAfEmpenhada indEmp;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Cancelada
	private String canc;
	private String cancChecked;
	private Boolean indCanc;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Planejamento
	private String plan;
	private String planChecked;
	private Boolean indPlan;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Assinatura
	private String ass;
	private String assChecked;
	private Boolean indAss;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Envio_Fornecedor
	private String envForn;
	private String envFornChecked;
	private Boolean indEnvioForn;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Recalculo_Autom
	private String recAut;
	private String recAutCkeched;
	private Boolean indRecalculoAutomatico;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Recalculo_Manual
	private String recManual;
	private String recManualChecked;
	private Boolean indRecalculoManual;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Entrega_Imediata
	private String entImed;
	private String entChecked;
	private Boolean indEntregaImediata;
	
	//Sco_Progr_Entrega_Itens_Af.Ind_Tramite_Interno
	private String tramInt;
	private String tramIntChecked;
	private Boolean indTramiteInterno;
	
	/**
	 * Valores abaixo usados somente quando o usuário clica em um registro na tela. 
	 */
	//Sco_Progr_Entrega_Itens_Af.dt_entrega
	private Date dtEntrega;
	
	//sco_af_pedidos. dt_envio_fornecedor
	private Date envioAFP;
	
	//Sco_Itens_Autorizacao_Forn.qtde_solicitada
	private Integer qtdItemAF;
	
	//sco_progr_entrega_itens_af.sum(qtde)
	private Integer qtdTotalProg;
	
	//sco_progr_entrega_itens_af.qtde_entregue
	private Integer qtdEntParc;
	
	//fcc_centro_custos.descricao
	private String descCCSol;
	
	//Sco_Solicitacoes_De_Compras.numero ou sco_solicitacoes_servico.numero
	private Integer nroSol;
	
	// Compra ou Servico
	private String solicitaoDesc = "Solicitacao de Compra";
	
	//sco_progr_entrega_itens_af.dt_assinatura
	private Date assinatura;
	
	//sco_justificativas.descricao
	private String justEmpenho;
	
	//sco_justificativas.descricao
	private String justEmpenhoTruncado;
	
	//Sco_Itens_Autorizacao_Forn.qtde_recebida
	private Integer qtdRecAF;	
	
	//sco_progr_entrega_itens_af. sum(qtde_entregue)
	private Integer totalEntregue;
	
	//sco_progr_entrega_itens_af.valor_efetivado
	private Double valorEfetivado;
	
	//fcc_centro_custos.descricao
	private String descCCApp;
	
	//sco_progr_entrega_itens_af.observacao
	private String obs;
	private Integer iafAfnNumero;
	private Integer iafNumero;	
	private DominioClassifABC subClassificacaoAbc;
	private String corPadrao;
	private String obsTruncado;
	

	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Short getCp() {
		return cp;
	}

	public void setCp(Short cp) {
		this.cp = cp;
	}

	public Integer getAfp() {
		return afp;
	}

	public void setAfp(Integer afp) {
		this.afp = afp;
	}

	public void setItemAF(Integer itemAF) {
		this.itemAF = itemAF;
	}

	public Integer getItemAF() {
		return itemAF;
	}

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public Date getPrevisaoDt() {
		return previsaoDt;
	}

	public void setPrevisaoDt(Date previsaoDt) {
		this.previsaoDt = previsaoDt;
	}

	public Integer getQtdParcela() {
		return qtdParcela;
	}

	public void setQtdParcela(Integer qtdParcela) {
		this.qtdParcela = qtdParcela;
	}

	public String getUmdForn() {
		return umdForn;
	}

	public void setUmdForn(String umdForn) {
		this.umdForn = umdForn;
	}

	public Double getQtdReceber() {
		return qtdReceber;
	}

	public void setQtdReceber(Double qtdReceber) {
		this.qtdReceber = qtdReceber;
	}

	public Short getAlm() {
		return alm;
	}

	public void setAlm(Short alm) {
		this.alm = alm;
	}

	public String getDescAlm() {
		return descAlm;
	}
	
	public String getSeqDescAlm(){
		return alm + HIFEN_ESPACADO + descAlm;
	}

	public void setDescAlm(String descAlm) {
		this.descAlm = descAlm;
	}

	public Integer getEspacoDisponivel() {
		return espacoDisponivel;
	}

	public void setEspacoDisponivel(Integer espacoDisponivel) {
		this.espacoDisponivel = espacoDisponivel;
	}

	public Integer getCodGrupoMat() {
		return codGrupoMat;
	}

	public void setCodGrupoMat(Integer codGrupoMat) {
		this.codGrupoMat = codGrupoMat;
	}

	public String getDescGrupoMat() {
		return descGrupoMat;
	}
	
	public String getSeqDescGrupo() {
		return  descGrupoMat+ HIFEN_ESPACADO +codGrupoMat;
	}

	public void setDescGrupoMat(String descGrupoMat) {
		this.descGrupoMat = descGrupoMat;
	}

	public DominioClassifABC getDominioABC() {
		return dominioABC;
	}
	
	public String getClassABC() {
		if(dominioABC != null && subClassificacaoAbc != null){
			return dominioABC.toString() + HIFEN_ESPACADO + subClassificacaoAbc.toString();
		}
		return "";
	}

	public void setDominioABC(DominioClassifABC dominioABC) {
		this.dominioABC = dominioABC;
	}

	public String getFornecedor() {
		return  getNumeroFornecedor() + HIFEN_ESPACADO + getRazaoSocialFornecedor();
	}

	public String getEmp() {
		return emp;
	}

	public void setEmp(String emp) {
		this.emp = emp;
	}

	public String getCanc() {
		return canc;
	}

	public void setCanc(String canc) {
		this.canc = canc;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getAss() {
		return ass;
	}

	public void setAss(String ass) {
		this.ass = ass;
	}

	public String getEnvForn() {
		return envForn;
	}

	public void setEnvForn(String envForn) {
		this.envForn = envForn;
	}

	public String getRecAut() {
		return recAut;
	}

	public void setRecAut(String recAut) {
		this.recAut = recAut;
	}

	public String getRecManual() {
		return recManual;
	}

	public void setRecManual(String recManual) {
		this.recManual = recManual;
	}

	public String getEntImed() {
		return entImed;
	}

	public void setEntImed(String entImed) {
		this.entImed = entImed;
	}

	public String getTramInt() {
		return tramInt;
	}

	public void setTramInt(String tramInt) {
		this.tramInt = tramInt;
	}

	public Date getDtEntrega() {
		return dtEntrega;
	}

	public void setDtEntrega(Date dtEntrega) {
		this.dtEntrega = dtEntrega;
	}

	public Date getEnvioAFP() {
		return envioAFP;
	}

	public void setEnvioAFP(Date envioAFP) {
		this.envioAFP = envioAFP;
	}

	public Integer getQtdItemAF() {
		return qtdItemAF;
	}

	public void setQtdItemAF(Integer qtdItemAF) {
		this.qtdItemAF = qtdItemAF;
	}

	public Integer getQtdTotalProg() {
		return qtdTotalProg;
	}

	public void setQtdTotalProg(Integer qtdTotalProg) {
		this.qtdTotalProg = qtdTotalProg;
	}

	public Integer getQtdEntParc() {
		return qtdEntParc;
	}

	public void setQtdEntParc(Integer qtdEntParc) {
		this.qtdEntParc = qtdEntParc;
	}

	public String getDescCCSol() {
		return descCCSol;
	}

	public void setDescCCSol(String descCCSol) {
		this.descCCSol = descCCSol;
	}

	public Date getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(Date assinatura) {
		this.assinatura = assinatura;
	}

	public String getJustEmpenho() {
		return justEmpenho;
	}

	public void setJustEmpenho(String justEmpenho) {
		this.justEmpenho = justEmpenho;
	}

	public Integer getQtdRecAF() {
		return qtdRecAF;
	}

	public void setQtdRecAF(Integer qtdRecAF) {
		this.qtdRecAF = qtdRecAF;
	}

	public Integer getTotalEntregue() {
		return totalEntregue;
	}

	public void setTotalEntregue(Integer totalEntregue) {
		this.totalEntregue = totalEntregue;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public String getDescCCApp() {
		return descCCApp;
	}

	public void setDescCCApp(String descCCApp) {
		this.descCCApp = descCCApp;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}
	
	public void setFatorConv(Integer fatorConv) {
		this.fatorConv = fatorConv;
	}

	public Integer getFatorConv() {
		return fatorConv;
	}

	public void setUmdMat(String umdMat) {
		this.umdMat = umdMat;
	}

	public String getUmdMat() {
		return umdMat;
	}

	public void setMatServ(String matServ) {
		this.matServ = matServ;
	}

	public String getMatServ() {
		matServ = matCodigo + HIFEN_ESPACADO + matNome;
		return matServ;
	}

	public void setSolicitaoDesc(String solicitaoDesc) {
		this.solicitaoDesc = solicitaoDesc;
	}

	public String getSolicitaoDesc() {
		return solicitaoDesc;
	}

	public void setNroSol(Integer nroSol) {
		this.nroSol = nroSol;
	}

	public Integer getNroSol() {
		return nroSol;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}

	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}

	public void setCnpjFornecedor(String cnpjFornecedor) {
		this.cnpjFornecedor = cnpjFornecedor;
	}

	public String getCnpjFornecedor() {
		return cnpjFornecedor;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getMatNome() {
		return matNome;
	}

	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

	public Integer getAf() {
		return af;
	}

	public void setAf(Integer af) {
		this.af = af;
	}

	public Long getCpfFornecedor() {
		return cpfFornecedor;
	}

	public void setCpfFornecedor(Long cpfFornecedor) {
		this.cpfFornecedor = cpfFornecedor;
	}

	public Long getCgcFornecedor() {
		return cgcFornecedor;
	}

	public void setCgcFornecedor(Long cgcFornecedor) {
		this.cgcFornecedor = cgcFornecedor;
	}

	public Integer getProgEntregaItemAFSeq() {
		return progEntregaItemAFSeq; 
	}

	public void setProgEntregaItemAFSeq(Integer progEntregaItemAFSeq) {
		this.progEntregaItemAFSeq = progEntregaItemAFSeq;
	}

	public void setServico(boolean isServico) {
		this.isServico = isServico;
	}

	public boolean isServico() {
		return isServico;
	}

	public void setIndEmp(DominioAfEmpenhada indEmp) {
		this.indEmp = indEmp;
	}

	public DominioAfEmpenhada getIndEmp() {
		return indEmp;
	}

	public void setIndCanc(Boolean indCanc) {
		this.indCanc = indCanc;
	}

	public Boolean getIndCanc() {
		return indCanc;
	}

	public void setIndPlan(Boolean indPlan) {
		this.indPlan = indPlan;
	}

	public Boolean getIndPlan() {
		return indPlan;
	}

	public void setIndAss(Boolean indAss) {
		this.indAss = indAss;
	}

	public Boolean getIndAss() {
		return indAss;
	}

	public void setIndEnvioForn(Boolean indEnvioForn) {
		this.indEnvioForn = indEnvioForn;
	}

	public Boolean getIndEnvioForn() {
		return indEnvioForn;
	}

	public void setIndRecalculoAutomatico(Boolean indRecalculoAutomatico) {
		this.indRecalculoAutomatico = indRecalculoAutomatico;
	}

	public Boolean getIndRecalculoAutomatico() {
		return indRecalculoAutomatico;
	}

	public void setIndRecalculoManual(Boolean indRecalculoManual) {
		this.indRecalculoManual = indRecalculoManual;
	}

	public Boolean getIndRecalculoManual() {
		return indRecalculoManual;
	}

	public void setIndEntregaImediata(Boolean indEntregaImediata) {
		this.indEntregaImediata = indEntregaImediata;
	}

	public Boolean getIndEntregaImediata() {
		return indEntregaImediata;
	}

	public void setIndTramiteInterno(Boolean indTramiteInterno) {
		this.indTramiteInterno = indTramiteInterno;
	}

	public Boolean getIndTramiteInterno() {
		return indTramiteInterno;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setSubClassificacaoAbc(DominioClassifABC subClassificacaoAbc) {
		this.subClassificacaoAbc = subClassificacaoAbc;
	}

	public DominioClassifABC getSubClassificacaoAbc() {
		return subClassificacaoAbc;
	}

	public void setCorPadrao(String corPadrao) {
		this.corPadrao = corPadrao;
	}

	public String getCorPadrao() {
		return corPadrao;
	}

	public String getEmpChecked() {
		return empChecked;
	}

	public void setEmpChecked(String empChecked) {
		this.empChecked = empChecked;
	}

	public String getCancChecked() {
		return cancChecked;
	}

	public void setCancChecked(String cancChecked) {
		this.cancChecked = cancChecked;
	}

	public String getPlanChecked() {
		return planChecked;
	}

	public void setPlanChecked(String planChecked) {
		this.planChecked = planChecked;
	}

	public String getAssChecked() {
		return assChecked;
	}

	public void setAssChecked(String assChecked) {
		this.assChecked = assChecked;
	}

	public String getEnvFornChecked() {
		return envFornChecked;
	}

	public void setEnvFornChecked(String envFornChecked) {
		this.envFornChecked = envFornChecked;
	}

	public String getRecAutCkeched() {
		return recAutCkeched;
	}

	public void setRecAutCkeched(String recAutCkeched) {
		this.recAutCkeched = recAutCkeched;
	}

	public String getRecManualChecked() {
		return recManualChecked;
	}

	public void setRecManualChecked(String recManualChecked) {
		this.recManualChecked = recManualChecked;
	}

	public String getEntChecked() {
		return entChecked;
	}

	public void setEntChecked(String entChecked) {
		this.entChecked = entChecked;
	}

	public String getTramIntChecked() {
		return tramIntChecked;
	}

	public void setTramIntChecked(String tramIntChecked) {
		this.tramIntChecked = tramIntChecked;
	}

	public String getJustEmpenhoTruncado() {
		if(justEmpenho == null){
			return null;	
		}
		if (justEmpenho.length() > 196) {
			justEmpenhoTruncado = justEmpenho.substring(0, 196) + "...";
			return justEmpenhoTruncado;
		}else{
			return justEmpenho;
		}
		
	}

	public void setJustEmpenhoTruncado(String justEmpenhoTruncado) {
		this.justEmpenhoTruncado = justEmpenhoTruncado;
	}

	public String getObsTruncado() {
		if(obs == null){
			return null;	
		}
		if (obs.length() > 196) {
			obsTruncado = obs.substring(0, 196) + "...";
			return obsTruncado;
		}else{
			return obs;
		}
		
	}

	public void setObsTruncado(String obsTruncado) {
		this.obsTruncado = obsTruncado;
	}
	
	
}
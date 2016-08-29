package br.gov.mec.aghu.faturamento.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.model.FatRegistro;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.VFatAssocProcCids;


@SuppressWarnings("PMD.CyclomaticComplexity")
public class VisualizarItensDoProcedimentoController extends ActionController {

	private static final long serialVersionUID = 1301602320165108832L;
	
	//utilizado na SB de CID
	private static final Integer limiteRegistrosSBCID = 500;
	private static final Integer TAB_1=0;
	private static final Integer TAB_2=1;
	private static final Integer TAB_3=2;
	private static final Integer TAB_4=3;
	private static final Integer TAB_5=4;
	private static final Integer TAB_6=5;
	private static final Integer TAB_7=6;
	private static final Integer TAB_8=7;
	private static final Integer TAB_9=8;
	private static final Integer TAB_10=9;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	//Navegação
	private String voltarPara;
	
	//Usado na excludencia
	private Integer seq;
	private Long codTabela;
	private String descricao;
	
	//Usado na suggestion
	private FatItensProcedHospitalar itemProcedHosp;
	
	private FatCbos cbo;
	
	private AghCid aghCid;
	
	private Integer abaSelecionada;
	
	private Integer abaSelecionadaDefault = 1;
		
	//Para controle de ativar(false)/desativar(true) as abas da tela.
	private Boolean [] arrayAbas = {true, true, true, true, true, true, true, true, true,true};

	//Para controle a execucao da pesquisa de cada aba ao clicar nas mesmas.
	// Executar a pesquisa da aba somente na primeira vez em que a mesma for
	// clicada.
	private Boolean [] arrayExecutarAbas = {true, true, true, true, true, true, true, true, true,true};
	
	private Boolean pesquisou;
	
	private Boolean exibirModal;
	
	AghParametros pTabela;
	
	private List<FatProcedimentoCbo> listaProcedimentosCbo;
	
	private List<AghCid> listaCids;  
	
	private List<FatCaractItemProcHosp> caracteristicasItemProcHosp;
	
	private List<FatCompatExclusItem> listaCompatExclusItem;

	private List<FatCompatExclusItem> listaCompatibiliza;
	
	private List<FatCompatExclusItem> listaExcludencia;
	
	private List<FatVlrItemProcedHospComps> listaVlrItemProced;
	
	private List<FatItensProcedHospitalar> listaProcedimentosParaCid;
	
	private List<FatProcedimentoCbo> listaProcedimentosParaCbo;//aba 'procedimentos para cbo'
    
	private List<FatRegistro> listaFatRegistro;
	
	public enum VisualizarItensDoProcedimentoControllerExceptionCode implements BusinessExceptionCode {
		EXCECAO_MAIS_DE_UM_FILTRO_INFORMADO, EXCECAO_INFORMAR_UM_FILTRO;
	}

	private static final Comparator<FatProcedimentoCbo> COMPARATOR_CBO_SEQ = new Comparator<FatProcedimentoCbo>() {
		@Override
		public int compare(FatProcedimentoCbo o1, FatProcedimentoCbo o2) {
			return o1.getCbo().getSeq().compareTo(o2.getCbo().getSeq());
		}
	};

	//FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString()+ "." + FatTipoCaractItens.Fields.CARACTERISTICA.toString()
	private static final Comparator<FatCaractItemProcHosp> COMPARATOR_TIPO_CARACTERISTICA_ITEM = new Comparator<FatCaractItemProcHosp>() {
		@Override
		public int compare(FatCaractItemProcHosp o1, FatCaractItemProcHosp o2) {
			
			StringBuilder strO1 = new StringBuilder();
			StringBuilder strO2 = new StringBuilder();
			
			if (o1 != null && o1.getId() != null && o1.getId().getTctSeq() != null) {
				strO1.append(o1.getId().getTctSeq().toString());
			}
			
			if (o2 != null && o2.getId() != null && o2.getId().getTctSeq() != null) {
				strO2.append(o2.getId().getTctSeq().toString());
			}
			
			if (o1.getTipoCaracteristicaItem() != null && o1.getTipoCaracteristicaItem().getCaracteristica() != null) {
				strO1.append('.').append(o1.getTipoCaracteristicaItem().getCaracteristica());
			}
			
			if (o2.getTipoCaracteristicaItem() != null && o2.getTipoCaracteristicaItem().getCaracteristica() != null) {
				strO2.append('.').append(o2.getTipoCaracteristicaItem().getCaracteristica());
			}
			
			return strO1.toString().compareTo(strO2.toString());
		}
	};
	
	@PostConstruct
	public void init() {		
		begin(conversation);
		
		if (this.pTabela == null) {
			this.inicio();
		}
	}
	
	public void inicio(){
		this.pesquisou = false;
		this.exibirModal = false;
		
		
		this.abaSelecionada = TAB_1;
		try{
			this.pTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void validarFiltrosPesquisa() throws ApplicationBusinessException {
		if(this.itemProcedHosp == null && this.cbo == null && this.aghCid == null){
			throw new ApplicationBusinessException(VisualizarItensDoProcedimentoControllerExceptionCode.EXCECAO_INFORMAR_UM_FILTRO);
		} else	if((this.itemProcedHosp != null && this.cbo != null) 
			|| (this.itemProcedHosp != null && this.aghCid != null)
			|| (this.cbo != null && this.aghCid != null)
		){
			throw new ApplicationBusinessException(VisualizarItensDoProcedimentoControllerExceptionCode.EXCECAO_MAIS_DE_UM_FILTRO_INFORMADO);
		}
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void pesquisar(){
		try{
			
			this.validarFiltrosPesquisa();
			
			AghParametros pConvenio = null;
			try{
				pConvenio = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			}catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}

			
			this.pesquisou = true;
			
			if(this.itemProcedHosp != null){
				//this.faturamentoFacade.refresh(this.itemProcedHosp);
				
				if(TAB_1.equals(this.abaSelecionada) && this.arrayExecutarAbas[0]){
					this.arrayExecutarAbas[0] = false;
					//Habilita todas as abas, exceto a de Proc para CIDs e Proc para CBOs.
					for(int i = 0; i < this.arrayAbas.length; i++){
						if( i != 3 && i != 5){
							this.arrayAbas[i] = false;
						}
					}
					
				}else if(TAB_2.equals(this.abaSelecionada) && this.arrayExecutarAbas[1]){
					this.arrayExecutarAbas[1] = Boolean.FALSE;
					// Pesquisa da aba 2.
					if (itemProcedHosp == null || itemProcedHosp.getId() == null) {
						setListaVlrItemProced(new ArrayList<FatVlrItemProcedHospComps>(0));
					} else {
						setListaVlrItemProced(faturamentoFacade.obterListaValorItemProcHospComp(itemProcedHosp.getId().getPhoSeq(), itemProcedHosp
								.getId().getSeq()));
						if (getListaVlrItemProced() == null || getListaVlrItemProced().isEmpty()) {
							setListaVlrItemProced(new ArrayList<FatVlrItemProcedHospComps>(0));
						} else {
							for (FatVlrItemProcedHospComps comps : getListaVlrItemProced()) {
					//			this.faturamentoFacade.refresh(comps);
								realizarTotalizacao(comps);
							}
						}
					}
				}else if(TAB_3.equals(this.abaSelecionada) && this.arrayExecutarAbas[2]){
					this.arrayExecutarAbas[2] = Boolean.FALSE;
					
					this.listaProcedimentosCbo = new ArrayList<FatProcedimentoCbo>(this.faturamentoFacade.listarProcedimentoCboPorIphSeqEPhoSeq(itemProcedHosp.getId().getSeq(), itemProcedHosp.getId().getPhoSeq()));
					
					Collections.sort(this.listaProcedimentosCbo, COMPARATOR_CBO_SEQ);
					
				}else if(TAB_5.equals(this.abaSelecionada) && this.arrayExecutarAbas[4]){
					this.arrayExecutarAbas[4] = Boolean.FALSE;
					
					this.listaCids = this.faturamentoFacade.listarPorItemProcedimentoHospitalarEConvenio(this.itemProcedHosp.getId().getPhoSeq(), this.itemProcedHosp.getId().getSeq(), pConvenio.getVlrNumerico().shortValue(), VFatAssocProcCids.Fields.CODIGO_CID.toString());
					
				} else if (TAB_7.equals(this.abaSelecionada) && this.arrayExecutarAbas[6]) {
					this.arrayExecutarAbas[6] = Boolean.FALSE;
					this.listaCompatibiliza = this.faturamentoFacade.listaCompatExclusItem(
							itemProcedHosp.getId().getPhoSeq(), itemProcedHosp.getId().getSeq(),
							DominioIndComparacao.R, DominioIndCompatExclus.ICP, DominioSituacao.A, FatCompatExclusItem.Fields.IPH_COD_TABELA.toString());
					
					for (FatCompatExclusItem compatibiliza : listaCompatibiliza) {
						compatibiliza.setCorVermelha(corFonteVermelha(compatibiliza.getItemProcedHosp().getId().getPhoSeq(), 
																	  compatibiliza.getItemProcedHosp().getId().getSeq(), 
																	  compatibiliza.getItemProcedHospCompatibiliza().getId().getPhoSeq(), 
																	  compatibiliza.getItemProcedHospCompatibiliza().getId().getSeq()));
					}
					

				}else if(TAB_8.equals(this.abaSelecionada) && this.arrayExecutarAbas[7]){
					this.arrayExecutarAbas[7] = Boolean.FALSE;
					
					this.listaCompatExclusItem = this.faturamentoFacade.listarFatCompatExclusItemPorIphCompatibilizaEIndSituacaoEIndComparacao(this.itemProcedHosp.getId().getPhoSeq(), this.itemProcedHosp.getId().getSeq(), DominioIndComparacao.R, DominioSituacao.A, 
							FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString()+ "." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), true);
					
				}else if(TAB_9.equals(this.abaSelecionada) && this.arrayExecutarAbas[8]){
					this.arrayExecutarAbas[8] = Boolean.FALSE;

					this.caracteristicasItemProcHosp = new ArrayList<FatCaractItemProcHosp>(this.faturamentoFacade.listarCaractItemProcHospPorSeqEPhoSeq(this.itemProcedHosp.getId().getPhoSeq(), this.itemProcedHosp.getId().getSeq()));

					// ITENS TABELA
					if (itemProcedHosp.getProcedimentoEspecial()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_PROCEDIMENTO_ESPECIAL"));
					}
					if (itemProcedHosp.getHcpaCadastrado()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_PROCEDIMENTO_CADASTRADO"));
					}
					if (itemProcedHosp.getConsulta()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_CONSULTA"));
					}
					if (itemProcedHosp.getExigeConsulta()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_EXIGE_CONSULTA"));
					}
					if (itemProcedHosp.getPsiquiatria()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_PSIQUIATRIA"));
					}
					if (itemProcedHosp.getCidadeObrigatoria()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_EXIGE_CID_SECUNDARIO"));
					}
					if (itemProcedHosp.getFaec()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_ALTA_COMPLEXIDADE"));
					}
					if (itemProcedHosp.getDcihTransplante()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_ESTRATEGICO"));
					}
					if (itemProcedHosp.getBuscaDoador()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_BUSCA_DOADOR"));
					}
					if (itemProcedHosp.getCobraExcedenteBpa()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_COBRA_EXCEDENTE_APAC_BPA"));
					}
					if (itemProcedHosp.getTipoAih5()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_AIH5"));
					}
					if (itemProcedHosp.getQuantidadeMaiorInternacao()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_QTD_MAIOR_INTERNACAO"));
					}
					if (itemProcedHosp.getExigeValor()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_EXIGE_VALOR"));
					}
					if (itemProcedHosp.getHospDia()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_HOSPITAL_DIA"));
					}
					if (itemProcedHosp.getAidsPolitraumatizado()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_AIDS"));
					}
					if (itemProcedHosp.getCobrancaConta()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_COBRANCA_CONTA"));
					}
					if (itemProcedHosp.getDadosParto()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_EXIGE_DADOS_PARTO"));
					}
					if (itemProcedHosp.getRealDifereSolic()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_REALIZADO_DIFERENTE_SOLICITADO"));
					}
					if (itemProcedHosp.getSolicDifereReal()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_SOLICITADO_DIFERENTE_REALIZADO"));
					}
					if (itemProcedHosp.getCobraProcedimentoEspecial()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_COBRANCA_PROCEDIMENTO_ESPECIAL"));
					}
					if (itemProcedHosp.getCirurgiaMultipla()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_CIRURGIA_MULTIPLA_POLITRAUMATIZADO"));
					}
					if (itemProcedHosp.getDiariaAcompanhante()) {
						caracteristicasItemProcHosp.add(criaCaracteristica("LABEL_DIARIA_ACOMPANHANTE"));
					}

					Collections.sort(this.caracteristicasItemProcHosp, COMPARATOR_TIPO_CARACTERISTICA_ITEM);

				}else if(TAB_10.equals(this.abaSelecionada) && this.arrayExecutarAbas[9]){
					this.arrayExecutarAbas[9] = Boolean.FALSE;
					this.listaFatRegistro = new ArrayList<FatRegistro>(this.faturamentoFacade.listarFatRegistroPorItensProcedimentoHospitalar(this.itemProcedHosp.getId().getPhoSeq(), this.itemProcedHosp.getId().getSeq()));
				}
			}else if(this.cbo != null && this.arrayExecutarAbas[3]){
				this.abaSelecionada = TAB_4;
				//this.faturamentoFacade.refresh(this.cbo);
				this.arrayAbas[3] = false;
				
				//Colocar aqui a pesquisa da aba 4.
				this.abaSelecionadaDefault = 4;
				 this.listaProcedimentosParaCbo = this.faturamentoFacade.listarProcedimentoCboPorCbo(this.cbo.getCodigo());
				
			}else if(this.aghCid != null && this.arrayExecutarAbas[5]){
				this.abaSelecionada = TAB_6;
				//this.faturamentoFacade.refresh(this.cid);
				this.arrayAbas[5] = false;
				
				//Colocar aqui a pesquisa da aba 6.
				this.abaSelecionadaDefault = 6;
				this.listaProcedimentosParaCid = this.faturamentoFacade.listarPorCidEConvenio(this.aghCid.getCodigo(), pConvenio.getVlrNumerico().shortValue(), FatItensProcedHospitalar.Fields.SEQ_ORDER.toString(), true);
			}
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private FatCaractItemProcHosp criaCaracteristica(final String nomeCaracteristica) {
		FatCaractItemProcHosp caract = new FatCaractItemProcHosp();
		FatTipoCaractItens tipoCar = new FatTipoCaractItens();
		tipoCar.setCaracteristica(getMensagem(nomeCaracteristica));
		caract.setValorChar("S");
		caract.setTipoCaracteristicaItem(tipoCar);
		return caract;
	}

	protected String getMensagem(final String key, final Object... args) {	
		String val = this.faturamentoFacade.obterMensagemResourceBundle(key);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				val = val.replaceAll("\\{" + i + "\\}", args[i].toString());
			}
		}
		return val;
	}
	
	//Usado no  botao limpar
	public void limparPesquisa(){
		this.abaSelecionada = TAB_1;
		this.abaSelecionadaDefault = 1;
		
		//Desabilita todas as abas.
		for(int i = 0; i < this.arrayAbas.length; i++){
			this.arrayAbas[i] = true;
		}
		
		//Habilita a execução das pesquisas para todas as abas.
		for(int i = 0; i < this.arrayExecutarAbas.length; i++){
			this.arrayExecutarAbas[i] = true;
		}
		
		this.itemProcedHosp = null;
		this.cbo = null;
		this.aghCid = null;
		
		this.pesquisou = Boolean.FALSE;	
	}
	
	public BigDecimal buscaValor(Short phoSeq, Integer iphSeq){
		BigDecimal valor = BigDecimal.ZERO;
		FatVlrItemProcedHospComps valorItem = null;
		List<FatVlrItemProcedHospComps> lista = this.faturamentoFacade.obterListaValorItemProcHospCompPorPhoIphAbertos(phoSeq, iphSeq);
		if(lista != null && !lista.isEmpty()){
			valorItem = lista.get(0);
			AghParametros pAmbulatorio = null;
			try{
				pAmbulatorio = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_AMBULATORIO);
			}catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			//getFatItensProcedHospitalar eh NotNull
			if(valorItem.getFatItensProcedHospitalar().getId().getPhoSeq().equals(pAmbulatorio.getVlrNumerico().shortValue()) && valorItem.getVlrProcedimento() != null){
				valor = valorItem.getVlrProcedimento();
			}else{
				if(valorItem.getVlrProcedimento() != null){
					valor = valorItem.getVlrProcedimento();
				}
				if(valorItem.getVlrServHospitalar() != null){
					valor = valor.add(valorItem.getVlrServHospitalar());
				}
				if(valorItem.getVlrServProfissional() != null){
					valor = valor.add(valorItem.getVlrServProfissional());
				}
				if(valorItem.getVlrSadt() != null){
					valor = valor.add(valorItem.getVlrSadt());
				}
				if(valorItem.getVlrAnestesista() != null){
					valor = valor.add(valorItem.getVlrAnestesista());
				}
			}
		}
		
		return valor;
	}
	
	public BigDecimal buscaValorCompatibiliza(Short phoSeq, Integer iphSeq) {
		BigDecimal valor = BigDecimal.ZERO;
		FatVlrItemProcedHospComps valorItem = null;
		List<FatVlrItemProcedHospComps> lista = this.faturamentoFacade
				.obterListaValorItemProcHospCompPorPhoIphAbertos(phoSeq, iphSeq);
		if (lista != null && !lista.isEmpty()) {
			valorItem = lista.get(0);
			
			if (valorItem.getId().getIphPhoSeq().equals((short) 4))  {
				if (valorItem.getVlrProcedimento() != null){
					valor = valorItem.getVlrProcedimento();
				}
			} else {
				if (valorItem.getVlrProcedimento() != null) {
					valor = valorItem.getVlrProcedimento();
				}
				if (valorItem.getVlrServHospitalar() != null) {
					valor = valor.add(valorItem.getVlrServHospitalar());
				}
				if (valorItem.getVlrServProfissional() != null) {
					valor = valor.add(valorItem.getVlrServProfissional());
				}
				if (valorItem.getVlrSadt() != null) {
					valor = valor.add(valorItem.getVlrSadt());
				}
				if (valorItem.getVlrAnestesista() != null) {
					valor = valor.add(valorItem.getVlrAnestesista());
				}
			}
		}

		return valor;
	}
	
	
	public Boolean corFonteVermelha(Short phoSeqCont, Integer iphSeqCont, Short phoSeqComp, Integer iphSeqComp) {
		if (!faturamentoFacade.verFatCompatItem(phoSeqCont, iphSeqCont, phoSeqComp, iphSeqComp).equals("N")){
			return true;
		}
		else{
			return false;
		}
	}

	public void pesquisarExcludencia(Short phoComp, Integer iphComp) {
		listaExcludencia = faturamentoFacade.pesquisarExcludencia(phoComp, iphComp, DominioIndComparacao.I, DominioSituacao.A);
		
		if (!listaExcludencia.isEmpty()) {
			seq = listaExcludencia.get(0).getItemProcedHosp().getId().getSeq();
			codTabela = listaExcludencia.get(0).getItemProcedHosp().getCodTabela();
			descricao = listaExcludencia.get(0).getItemProcedHosp().getDescricao();
		}
		this.exibirModal = true;
	}
	
	public void fecharModal() {
		this.exibirModal = false;
	}
	
//#############################################//
//### Metodos abaixo usados nas suggestions ###//
//#############################################//
	
	//usado na suggestion de IPH
	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(String objPesquisa){
		return this.returnSGWithCount(this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(objPesquisa, this.pTabela.getVlrNumerico().shortValue(), FatItensProcedHospitalar.Fields.COD_TABELA.toString()),listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa));
	}
	
	//usado na suggestion de IPH
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
		return this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, this.pTabela.getVlrNumerico().shortValue());
	}
	
	public List<FatCbos> listarCbos(String objPesquisa){
		try {
			return this.returnSGWithCount(this.faturamentoFacade.listarCbos(objPesquisa),listarCbosCount(objPesquisa));
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return new ArrayList<FatCbos>();
	}

	public Long listarCbosCount(String objPesquisa){
		return this.faturamentoFacade.listarCbosCount(objPesquisa);
	}


	 //Método para pesquisar CIDs na suggestion 
	public List<AghCid> pesquisarCids(String param) {
		return this.returnSGWithCount(faturamentoFacade.pesquisarCidsPorDescricaoOuCodigo(param, limiteRegistrosSBCID, AghCid.Fields.DESCRICAO.toString()),pesquisarCidsCount(param));
	}
	
	//Método para pesquisar CIDs na suggestion
	public Long pesquisarCidsCount(String param) {
		return faturamentoFacade.pesquisarCidsPorDescricaoOuCodigoCount(param);
	}	
	
	private void realizarTotalizacao(FatVlrItemProcedHospComps comps) {
		comps.setVlrTotalAmb(comps.getVlrProcedimento() == null ? BigDecimal.ZERO : comps.getVlrProcedimento());
		comps.setVlrTotalInt(somarTotal(comps.getVlrServHospitalar() == null ? BigDecimal.ZERO : comps.getVlrServHospitalar(),
				comps.getVlrServProfissional()));
	}

	private BigDecimal somarTotal(BigDecimal origem, final BigDecimal... adicionado) {
		if (adicionado != null && adicionado.length > 0) {
			for (final BigDecimal add : adicionado) {
				if (add != null) {
					origem = origem.add(add);
				}
			}
		}
		return origem;
	}
	
	public BigDecimal valorTotalInt(BigDecimal procedimento, BigDecimal servHospitalar){
		return procedimento.add(servHospitalar);
	}
	
	public String voltar(){
		return getVoltarPara();
	}
	
//############################	
//### GETTERs and SETTERs ####
//############################
	
	public FatItensProcedHospitalar getItemProcedHosp() {
		return itemProcedHosp;
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		this.itemProcedHosp = itemProcedHosp;
	}

	public FatCbos getCbo() {
		return cbo;
	}

	public void setCbo(FatCbos cbo) {
		this.cbo = cbo;
	}

	public Boolean[] getArrayAbas() {
		return arrayAbas;
	}

	public void setArrayAbas(Boolean[] arrayAbas) {
		this.arrayAbas = arrayAbas;
	}

	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public AghParametros getpTabela() {
		return pTabela;
	}

	public void setpTabela(AghParametros pTabela) {
		this.pTabela = pTabela;
	}

	public Boolean[] getArrayExecutarAbas() {
		return arrayExecutarAbas;
	}

	public void setArrayExecutarAbas(Boolean[] arrayExecutarAbas) {
		this.arrayExecutarAbas = arrayExecutarAbas;
	}

	public List<FatProcedimentoCbo> getListaProcedimentosCbo() {
		return listaProcedimentosCbo;
	}

	public void setListaProcedimentosCbo(
			List<FatProcedimentoCbo> listaProcedimentosCbo) {
		this.listaProcedimentosCbo = listaProcedimentosCbo;
	}

	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}
	
	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}
	
	public Integer getAbaSelecionadaDefault() {
		return abaSelecionadaDefault;
	}

	public void setAbaSelecionadaDefault(Integer abaSelecionadaDefault) {
		this.abaSelecionadaDefault = abaSelecionadaDefault;
	}

	public List<AghCid> getListaCids() {
		return listaCids;
	}

	public void setListaCids(List<AghCid> listaCids) {
		this.listaCids = listaCids;
	}

	public List<FatCaractItemProcHosp> getCaracteristicasItemProcHosp() {
		return caracteristicasItemProcHosp;
	}

	public void setCaracteristicasItemProcHosp(
			List<FatCaractItemProcHosp> caracteristicasItemProcHosp) {
		this.caracteristicasItemProcHosp = caracteristicasItemProcHosp;
	}

	public void setListaVlrItemProced(List<FatVlrItemProcedHospComps> listaVlrItemProced) {
		this.listaVlrItemProced = listaVlrItemProced;
	}

	public List<FatVlrItemProcedHospComps> getListaVlrItemProced() {
		return listaVlrItemProced;
	}

	public List<FatCompatExclusItem> getListaCompatExclusItem() {
		return listaCompatExclusItem;
	}

	public void setListaCompatExclusItem(
			List<FatCompatExclusItem> listaCompatExclusItem) {
		this.listaCompatExclusItem = listaCompatExclusItem;
	}

	public List<FatItensProcedHospitalar> getListaProcedimentosParaCid() {
		return listaProcedimentosParaCid;
	}

	public void setListaProcedimentosParaCid(List<FatItensProcedHospitalar> listaProcedimentosParaCid) {
		this.listaProcedimentosParaCid = listaProcedimentosParaCid;
	}

	public List<FatProcedimentoCbo> getListaProcedimentosParaCbo() {
		return listaProcedimentosParaCbo;
	}

	public void setListaProcedimentosParaCbo(List<FatProcedimentoCbo> listaProcedimentosParaCbo) {
		this.listaProcedimentosParaCbo = listaProcedimentosParaCbo;
	}

	public List<FatCompatExclusItem> getListaCompatibiliza() {
		return listaCompatibiliza;
	}

	public void setListaCompatibiliza(List<FatCompatExclusItem> listaCompatibiliza) {
		this.listaCompatibiliza = listaCompatibiliza;
	}

	public List<FatCompatExclusItem> getListaExcludencia() {
		return listaExcludencia;
	}

	public void setListaExcludencia(List<FatCompatExclusItem> listaExcludencia) {
		this.listaExcludencia = listaExcludencia;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public AghCid getAghCid() {
		return aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public List<FatRegistro> getListaFatRegistro() {
		return listaFatRegistro;
	}

	public void setListaFatRegistro(List<FatRegistro> listaFatRegistro) {
		this.listaFatRegistro = listaFatRegistro;
	}
	
}
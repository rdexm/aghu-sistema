package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.faturamento.action.ManterCidContaHospitalarController.ManterCidContaHospitalarControllerExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioSumarioAltaController.Subtitulo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class LancarItensContaHospitalarPaginatorController extends ActionController{

	private static final Log LOG = LogFactory.getLog(LancarItensContaHospitalarPaginatorController.class);

	private static final long serialVersionUID = 3568019868226513611L;
	
	private static final Integer VALOR_ACCORDION_ABERTO = 0;
	private static final Integer VALOR_ACCORDION_FECHADO = -1;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	protected IComprasFacade comprasFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	private Integer cthSeq;

	private VFatContaHospitalarPac contaHospitalarView;

	private FatContasHospitalares contaHospitalar;

	private Boolean exibirBotaoIncluirItem = Boolean.TRUE;

	private Boolean somenteLeitura = Boolean.FALSE;

	private Boolean exibirListagem = Boolean.FALSE;

	private Boolean exibirSumario = Boolean.FALSE;

	private Integer atdSeq;

	private Integer apaSeq;

	private Integer prontuario;

	private Integer pacCodigo;

	private String nome;

	// FILTRO
	private VFatAssociacaoProcedimento procedimentoHospitalar;

	private DominioSituacaoItenConta situacao;

	private AghUnidadesFuncionais unidadeFuncional;

	private Date dthrRealizado;

	private DominioIndOrigemItemContaHospitalar origem;

	private Boolean fromEncerramentoPreviaConta;

	private Boolean fromManterContaHospitalar;

	private FatItemContaHospitalar itemExcluirNota;
	
	private FatItemContaHospitalar selection;

	private final String PAGE_CONSULTAR_CONTA_HOSPITALAR = "consultarContaHospitalar";

	private final String PAGE_MANTER_CONTA_HOSPITALAR = "manterContaHospitalar";

	private final String PAGE_MANTER_CID_CONTA_HOSPITALAR = "manterCidContaHospitalar";

	private String urlSumarioAlta;
	
	private final static String URL_SUMARIO_ALTA_DEFAULT = "/aghu/pages/prescricaomedica/relatorios/relatorioSumarioAltaPdf.xhtml";
	
	private final String PAGE_MANTER_NOTA_FISCAL = "manterNotaFiscal";

	private final String PAGE_LANCAR_ITENS_CONTA_HOSPITALAR = "faturamento-lancarItensContaHospitalar";
	
	private final String PAGE_LANCAR_ITENS_CONTA_HOSPITALAR_LIST = "faturamento-lancarItensContaHospitalarList";

	private AghCid cidPrincipal;

	private AghCid cidSecundario;

	private FatCidContaHospitalar fatCidPrincipalContaHospitalar;

	private FatCidContaHospitalar fatCidSecundarioContaHospitalar;
	
	private List<FatItemContaHospitalar> listResult;

	private boolean gravouLancarItemContaHosp;
	
	private Integer togglePesquisaOpened = 0;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		
		
		if (this.cthSeq != null) {
		
			limparPesquisa();
		
			if (fromEncerramentoPreviaConta == null) {
				fromEncerramentoPreviaConta = Boolean.FALSE;
			}

			if (fromManterContaHospitalar == null) {
				fromManterContaHospitalar = Boolean.FALSE;
			}

			if (somenteLeitura != null && somenteLeitura) {
				exibirBotaoIncluirItem = false;
			}

			contaHospitalarView = faturamentoFacade.obterContaHospitalarPaciente(cthSeq);
			contaHospitalar = faturamentoFacade.obterContaHospitalar(cthSeq, FatContasHospitalares.Fields.CONTA_INTERNACAO);
			
			//Quando paciente for doador de orgãos pode não ter prontuário.
			if(contaHospitalarView.getPacProntuario() != null){
				prontuario = Integer.valueOf(contaHospitalarView.getPacProntuario());
			}
			
			pacCodigo = Integer.valueOf(contaHospitalarView.getPacCodigo());
			nome = new String(contaHospitalarView.getPacNome());

			if (this.contaHospitalar != null && !this.faturamentoFacade.listarContasInternacao(cthSeq).isEmpty()) {
				final FatContasInternacao fatContasInternacao = (FatContasInternacao) this.faturamentoFacade.listarContasInternacao(cthSeq)
						.toArray()[0];

				if (fatContasInternacao.getInternacao() != null) {
					AinInternacao internacao = internacaoFacade.obterInternacao(fatContasInternacao.getInternacao().getSeq());
					this.atdSeq = internacao.getAtendimento().getSeq();
					exibirSumario = prescricaoMedicaFacade.obterAltaSumariosAtivoConcluido(this.atdSeq) != null ? true : false;
				}
			}
			if (exibirListagem) {
				this.pesquisar();
				recuperarSelection();
			}

			popularCidsPrimarioSecundario();
			
			togglePesquisaOpened = VALOR_ACCORDION_ABERTO;
		}
	}

	//#49262
	private void recuperarSelection() {
		if(gravouLancarItemContaHosp && selection != null){
			for (FatItemContaHospitalar fatItem : listResult) {
				if(fatItem.getId().getCthSeq().equals(selection.getId().getCthSeq()) && fatItem.getId().getSeq().equals(selection.getId().getSeq())){
					selection = fatItem;
					break;
				}
			}
		}
		gravouLancarItemContaHosp = false;
	}

	private void popularCidsPrimarioSecundario(){
		FatCidContaHospitalarId idPrincipal = new FatCidContaHospitalarId(cthSeq, null, DominioPrioridadeCid.P);
		fatCidPrincipalContaHospitalar = new FatCidContaHospitalar();
		fatCidPrincipalContaHospitalar.setId(idPrincipal);
		FatCidContaHospitalarId idSecundario = new FatCidContaHospitalarId(cthSeq, null, DominioPrioridadeCid.S);
		fatCidSecundarioContaHospitalar = new FatCidContaHospitalar();
		fatCidSecundarioContaHospitalar.setId(idSecundario);
		List<FatCidContaHospitalar> lista = this.faturamentoFacade.pesquisarCidContaHospitalar(0, 10, null, false, cthSeq);
		for(FatCidContaHospitalar cidConta: lista){
			if(DominioPrioridadeCid.P.equals(cidConta.getId().getPrioridadeCid())){
				fatCidPrincipalContaHospitalar = cidConta;
			} else if (DominioPrioridadeCid.S.equals(cidConta.getId().getPrioridadeCid())){
				fatCidSecundarioContaHospitalar = cidConta;
			}
		}
		
		cidPrincipal = fatCidPrincipalContaHospitalar.getCid();
		cidSecundario = fatCidSecundarioContaHospitalar.getCid();
	}

	public void collapseTogglePesquisa(){
		if(togglePesquisaOpened == VALOR_ACCORDION_ABERTO){
			togglePesquisaOpened=VALOR_ACCORDION_FECHADO;
		} else {
			togglePesquisaOpened=VALOR_ACCORDION_ABERTO;
		}	
	}
	
	public void excluirNotaFiscal() {
		if (getItemExcluirNota() == null) {
			apresentarMsgNegocio(Severity.INFO, "SELECIONE_CONTA_EXCLUSAO");
		} else {
			try {
				comprasFacade.removerNotaFiscal(getItemExcluirNota());
				getItemExcluirNota().setItemRmps(null);
				apresentarMsgNegocio(Severity.INFO, "VALORES_NOTA_FISCAL_EXLCUIDO_SUCESSO");
			} catch (final BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void pesquisar() {
		try {
			
			listResult = faturamentoFacade.listarItensContaHospitalar(
					cthSeq, 
					(procedimentoHospitalar != null) ? procedimentoHospitalar.getProcedimentoHospitalarInterno().getSeq() : null, 
					unidadeFuncional != null ? unidadeFuncional.getSeq() : null, 
					dthrRealizado, 
					situacao, origem, 
					Boolean.TRUE, //removerFiltro, 
					null); // parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS)
			
			for (final FatItemContaHospitalar itemConta : listResult) {
				itemConta.setManterNotaFiscal(
						//estoqueFacade.isFatItensContaHospitalarComMateriaisOrteseseProteses(itemConta.getId().getCthSeq(), itemConta.getId().getSeq(), itemConta.getProcedimentoHospitalarInterno().getSeq()) &&
						estoqueFacade.isHabilitarExcluirHCPA());
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		exibirBotaoIncluirItem = true;
		if (somenteLeitura != null && somenteLeitura) {
			exibirBotaoIncluirItem = false;
		}

		if(listResult != null && listResult.size() > 0){
			togglePesquisaOpened = VALOR_ACCORDION_FECHADO;
		}
		exibirListagem = false;
	}	

	public void limparPesquisa() {
		this.procedimentoHospitalar = null;
		this.situacao = null;
		this.unidadeFuncional = null;
		this.dthrRealizado = null;
		this.origem = null;
		// super.setOrder("dthrRealizado desc");
		togglePesquisaOpened = VALOR_ACCORDION_ABERTO;
		this.pesquisar();
		contaHospitalarView = faturamentoFacade.obterContaHospitalarPaciente(cthSeq);
		prontuario = Integer.valueOf(contaHospitalarView.getPacProntuario());
		pacCodigo = Integer.valueOf(contaHospitalarView.getPacCodigo());
		nome = new String(contaHospitalarView.getPacNome());
	}

	public String redirecionarLancarItensContaHospitalar(FatItemContaHospitalar item) {
		selection = item;
		return PAGE_LANCAR_ITENS_CONTA_HOSPITALAR;
	}
	
	public String redirecionarLancarItensContaHospitalar() {
		return PAGE_LANCAR_ITENS_CONTA_HOSPITALAR;
	}

	public String redirecionarManterNotaFiscal() {
		return PAGE_MANTER_NOTA_FISCAL;
	}

	public String iniciarInclusaoCid() {
		return PAGE_MANTER_CID_CONTA_HOSPITALAR;
	}

	public void sumarioAlta() {
		
		StringBuilder stringBuilderUrlSumarioAlta = new StringBuilder(250)
			.append(URL_SUMARIO_ALTA_DEFAULT)
			.append("?atdSeq=")
			.append(atdSeq)
			.append(";previa=")
			.append(false)
			.append(";subtitulo=")
			.append(Subtitulo.REIMPRESSAO.toString())
			.append(";voltarPara=")
			.append(PAGE_LANCAR_ITENS_CONTA_HOSPITALAR_LIST)
			;
		
		urlSumarioAlta = stringBuilderUrlSumarioAlta.toString();
		
		RequestContext.getCurrentInstance().execute("jsExecutaBotaoSumarioAlta()");
	}

	public List<VFatAssociacaoProcedimento> listarProcedimentosSUS(final String strPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarAssociacaoProcedimentoSUSItem(strPesquisa, null, cthSeq, false),listarProcedimentosSUSCount(strPesquisa));
		} catch (final BaseException e) {
			return new ArrayList<VFatAssociacaoProcedimento>();
		}
	}

	public Integer listarProcedimentosSUSCount(final String strPesquisa) {
		try {
			return faturamentoFacade.listarAssociacaoProcedimentoSUSItemCount(strPesquisa, null, cthSeq, false).intValue();
		} catch (final BaseException e) {
			return 0;
		}
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(strPesquisa),pesquisarUnidadeFuncionalCount(strPesquisa));
	}

	public Integer pesquisarUnidadeFuncionalCount(final String strPesquisa) {
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(strPesquisa).intValue();
	}

	/**
	 * Realizar as consistências antes de chamar a tela de Sumário Alta
	 * 
	 * @return
	 */
	public String realizarChamadaSumarioAlta() {
		String retorno = "erro";
		try {
			if (atdSeq != null) {
				this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(atdSeq);
				retorno = "sumarioAlta";
			}
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	public String fecharPesquisa() {
		if (fromEncerramentoPreviaConta) {
			return "faturamento-encerramentoPreviaConta";

		} else {
			if (somenteLeitura != null && somenteLeitura && !fromManterContaHospitalar) {
				return PAGE_CONSULTAR_CONTA_HOSPITALAR;
			} else {
				return PAGE_MANTER_CONTA_HOSPITALAR;
			}
		}
	}

	/**
	 * Método para pesquisar CIDs na suggestion da tela. São pesquisados somente
	 * os 300 primeiros registros.
	 * 
	 * @param param
	 * @return Lista de objetos AghCid
	 */
	public List<AghCid> pesquisarCidsPrincipal(String param) {
		Integer phiSeq = null;
		if (contaHospitalar != null && contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null){
			phiSeq = contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq();
		}
		return this.returnSGWithCount(faturamentoFacade.pesquisarCidsPorSSMDescricaoOuCodigo(phiSeq, param,
				Integer.valueOf(300)), pesquisarCidsPrincipalCount(param));
	}

	public Long pesquisarCidsPrincipalCount(String param) {
		Integer phiSeq = null;
		if (contaHospitalar != null && contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null){
			phiSeq = contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq();
		}
		return faturamentoFacade.pesquisarCidsPorSSMDescricaoOuCodigoCount(phiSeq, param);
	}

	/**
	 * Método para pesquisar CIDs na suggestion da tela. São pesquisados somente
	 * os 300 primeiros registros.
	 * 
	 * @param param
	 * @return Lista de objetos AghCid
	 */

	public List<AghCid> pesquisarCidsSecundario(String param) {
		return this.returnSGWithCount(faturamentoFacade.pesquisarCidsPorDescricaoOuCodigo(param,
				Integer.valueOf(300)), pesquisarCidsSecundarioCount(param));
	}

	public Long pesquisarCidsSecundarioCount(String param) {
		return faturamentoFacade.pesquisarCidsPorDescricaoOuCodigoCount(param);
	}

	public void gravarCids(){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}

		try{
			final Date dataFimVinculoServidor = new Date();
			List<FatCidContaHospitalar> lista = this.faturamentoFacade.pesquisarCidContaHospitalar(0, 10, null, false, cthSeq);
			for(FatCidContaHospitalar cidConta: lista){
				removerCidPreGravar(cidConta.getId());
			}
			boolean gravou = false;
			if(cidPrincipal != null){
				fatCidPrincipalContaHospitalar.setCid(cidPrincipal);
				fatCidPrincipalContaHospitalar.getId().setCidSeq(cidPrincipal.getSeq());
				faturamentoFacade.persistirCidContaHospitalar(fatCidPrincipalContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
				gravou = true;
			}
			if(cidSecundario != null){
				fatCidSecundarioContaHospitalar.setCid(cidSecundario);
				fatCidSecundarioContaHospitalar.getId().setCidSeq(cidSecundario.getSeq());
				faturamentoFacade.persistirCidContaHospitalar(fatCidSecundarioContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
				gravou = true;
			}
			if(gravou){
				apresentarMsgNegocio(Severity.INFO,
					ManterCidContaHospitalarControllerExceptionCode.CID_CONTA_HOSPITALAR_CADASTRADO_SUCESSO.toString());
			}else {
				apresentarMsgNegocio(Severity.INFO,
					ManterCidContaHospitalarControllerExceptionCode.CID_CONTA_HOSPITALAR_CADASTRADO_SEM_SUCESSO.toString());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void removerCidPreGravar(FatCidContaHospitalarId id) {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			final Date dataFimVinculoServidor = new Date();
			faturamentoFacade.removerCidContaHospitalar(id.getCthSeq(), id.getCidSeq(), id.getPrioridadeCid(), nomeMicrocomputador, dataFimVinculoServidor);
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(final Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public VFatContaHospitalarPac getContaHospitalarView() {
		return contaHospitalarView;
	}

	public void setContaHospitalarView(final VFatContaHospitalarPac contaHospitalarView) {
		this.contaHospitalarView = contaHospitalarView;
	}

	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(final FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public VFatAssociacaoProcedimento getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(final VFatAssociacaoProcedimento procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public DominioSituacaoItenConta getSituacao() {
		return situacao;
	}

	public void setSituacao(final DominioSituacaoItenConta situacao) {
		this.situacao = situacao;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(final Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public DominioIndOrigemItemContaHospitalar getOrigem() {
		return origem;
	}

	public void setOrigem(final DominioIndOrigemItemContaHospitalar origem) {
		this.origem = origem;
	}

	public Boolean getExibirBotaoIncluirItem() {
		return exibirBotaoIncluirItem;
	}

	public void setExibirBotaoIncluirItem(final Boolean exibirBotaoIncluirItem) {
		this.exibirBotaoIncluirItem = exibirBotaoIncluirItem;
	}

	public Boolean getExibirListagem() {
		return exibirListagem;
	}

	public void setExibirListagem(final Boolean exibirListagem) {
		this.exibirListagem = exibirListagem;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(final Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getApaSeq() {
		return apaSeq;
	}

	public void setApaSeq(final Integer apaSeq) {
		this.apaSeq = apaSeq;
	}

	public Boolean getExibirSumario() {
		return exibirSumario;
	}

	public void setExibirSumario(final Boolean exibirSumario) {
		this.exibirSumario = exibirSumario;
	}

	public Boolean getSomenteLeitura() {
		return somenteLeitura;
	}

	public void setSomenteLeitura(final Boolean somenteLeitura) {
		this.somenteLeitura = somenteLeitura;
	}

	public Boolean getFromEncerramentoPreviaConta() {
		return fromEncerramentoPreviaConta;
	}

	public void setFromEncerramentoPreviaConta(final Boolean fromEncerramentoPreviaConta) {
		this.fromEncerramentoPreviaConta = fromEncerramentoPreviaConta;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(final Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(final Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public Boolean getFromManterContaHospitalar() {
		return fromManterContaHospitalar;
	}

	public void setFromManterContaHospitalar(final Boolean fromManterContaHospitalar) {
		this.fromManterContaHospitalar = fromManterContaHospitalar;
	}

	public void setItemExcluirNota(final FatItemContaHospitalar itemExcluirNota) {
		this.itemExcluirNota = itemExcluirNota;
	}

	public FatItemContaHospitalar getItemExcluirNota() {
		return itemExcluirNota;
	}	
	public AghCid getCidPrincipal() {

		return cidPrincipal;

	}

	public void setCidPrincipal(AghCid cidPrincipal) {
		this.cidPrincipal = cidPrincipal;
	}

	public AghCid getCidSecundario() {
		return cidSecundario;
	}

	public void setCidSecundario(AghCid cidSecundario) {
		this.cidSecundario = cidSecundario;
	}

	public Integer getTogglePesquisaOpened() {
		return togglePesquisaOpened;
	}

	public void setTogglePesquisaOpened(Integer togglePesquisaOpened) {
		this.togglePesquisaOpened = togglePesquisaOpened;
	}
	
	public List<FatItemContaHospitalar> getListResult() {
		return listResult;
	}

	public void setListResult(List<FatItemContaHospitalar> listResult) {
		this.listResult = listResult;
	}

	public String getFiltrosPesquisa() {
		StringBuilder filtro = new StringBuilder(83);
		if(situacao != null){
			filtro.append(" | Situação: ".concat(situacao.getDescricao()));
		}
		if(dthrRealizado != null){
			filtro.append(" | Data Realizado: ".concat(DateUtil.obterDataFormatada(dthrRealizado, "dd/MM/yyyy")));
		}
		if(origem != null){
			filtro.append(" | Origem: ".concat(origem.getDescricao()));
		}
		if(procedimentoHospitalar != null
				&& procedimentoHospitalar.getProcedimentoHospitalarInterno() != null
				&& procedimentoHospitalar.getProcedimentoHospitalarInterno().getSeq()!= null){
			filtro.append(" | PHI: ".concat(procedimentoHospitalar.getProcedimentoHospitalarInterno().getSeq().toString()));
		}
		if(unidadeFuncional != null && unidadeFuncional.getSeq() != null){
			filtro.append(" | Unidade Funcional: ".concat(unidadeFuncional.getDescricao()));
		}
		return filtro.toString();
	}

	public FatItemContaHospitalar getSelection() {
		return selection;
	}

	public void setSelection(FatItemContaHospitalar selection) {
		this.selection = selection;
	}

	public boolean isGravouLancarItemContaHosp() {
		return gravouLancarItemContaHosp;
	}

	public void setGravouLancarItemContaHosp(boolean gravouLancarItemContaHosp) {
		this.gravouLancarItemContaHosp = gravouLancarItemContaHosp;
	}

	public String getUrlSumarioAlta() {
		return urlSumarioAlta;
	}

	public void setUrlSumarioAlta(String urlSumarioAlta) {
		this.urlSumarioAlta = urlSumarioAlta;
	}
	
}

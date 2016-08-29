package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterNumeroAihsPaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -9145133475934963024L;

	@Inject @Paginator
	private DynamicDataModel<FatAih> dataModel;
	
	private static final Log LOG = LogFactory.getLog(ManterNumeroAihsPaginatorController.class);
	
	private final String PAGE_CADASTRAR_AIHS = "cadastrarAihs";
	private final String PAGE_MANTER_NUMERO_AIHS = "manterNumeroAihs";
	
	private FatAih fatAih;
	private FatAih entity;
	private String situacao;
	private boolean inicial = true;
	private boolean exibirBotaoNovo;
	private List<DominioSituacaoAih> situacoes;

	private Long nroAihInicial;
	private Long nroAihFinal;
	private boolean confirmarGravacao;
	private boolean carregarInseridos;
	private List<Long> novasAihs;
	private Long nroAihsInsercao;
	private Long nroAihsRecusadas;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@PostConstruct
	public void inicio() {
		begin(conversation, true);
		
		if (isInicial()) {
			setInicial(false);		
			setSituacoes(new ArrayList<DominioSituacaoAih>());
			getSituacoes().add(DominioSituacaoAih.U);
			getSituacoes().add(DominioSituacaoAih.B);
			getSituacoes().add(DominioSituacaoAih.V);
		}
		
		this.init();
	}
	
	private void init() {
		setFatAih(new FatAih());
		getFatAih().setContaHospitalar(new FatContasHospitalares());
		setExibirBotaoNovo(false);
		setNroAihInicial(null);
		setNroAihFinal(null);
		setConfirmarGravacao(false);		
	}

	public void atualizarSituacao() {
		DominioSituacaoAih situacao = DominioSituacaoAih.valueOf(getSituacao());
		if (!situacao.equals(getEntity().getIndSituacao())) {
			try {
				faturamentoFacade.atualizarSituacaoFatAih(getEntity(), situacao);
				this.apresentarMsgNegocio(Severity.INFO, "VALORES_SITUACAO_AIH_ALTERADA_SUCESSO");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public String iniciarGravacao() {
		if (getNroAihInicial() > getNroAihFinal()) {
			this.apresentarMsgNegocio(Severity.ERROR, "AIH_INICIAL_MAIOR_FINAL");
			return PAGE_MANTER_NUMERO_AIHS;
		} else {
			novasAihs = faturamentoFacade.gerarFatAih(getNroAihInicial(), getNroAihFinal());
			setNroAihsInsercao(Long.valueOf(novasAihs.size()));
			setNroAihsRecusadas(Long.valueOf(getNroAihFinal() - getNroAihInicial() + 1 - novasAihs.size()));
			setConfirmarGravacao(true);
			this.openDialog("modalConfirmacaoWG");
		}
		return null;
	}

	public String gravar() {
		try {
			this.dataModel.reiniciarPaginator();
			Integer nrInseridos = faturamentoFacade.gravarFatAihLote(novasAihs);
			if (nrInseridos == 0 || novasAihs == null || novasAihs.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO, "AIH_NENHUMA_CADASTRADA", nrInseridos);
				setNroAihInicial(null);
				setNroAihFinal(null);
				setCarregarInseridos(false);
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "AIH_GERADAS_SUCESSO", nrInseridos);
				setNroAihInicial(novasAihs.get(0));
				setNroAihFinal(novasAihs.get(novasAihs.size() - 1));
				setCarregarInseridos(true);
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return PAGE_MANTER_NUMERO_AIHS;
	}

	public String novo() {
		setNroAihInicial(null);
		setNroAihFinal(null);
		setConfirmarGravacao(false);
		this.closeDialog("modalConfirmacaoWG");
		return PAGE_CADASTRAR_AIHS;
	}

	public String cancelar() {
		setConfirmarGravacao(false);
		this.closeDialog("modalConfirmacaoWG");
		setNroAihInicial(null);
		setNroAihFinal(null);
		novasAihs = null;
		setCarregarInseridos(false);
		return PAGE_MANTER_NUMERO_AIHS;
	}

	/*@Override
	public DataModel getDataModel() {
		String orderProperty = null;
		String orderDirection = null;

		if (getOrder() != null) {
			int i = getOrder().indexOf(' ');
			orderProperty = getOrder().substring(0, i);
			orderDirection = getOrder().substring(i + 1);
		}
		if (isCarregarInseridos()) {
			List<FatAih> retorno = faturamentoFacade.pesquisarAihsIntervalo(getFirstResult(), getMaxResults(), orderProperty,
					"asc".equalsIgnoreCase(orderDirection), getNroAihInicial(), getNroAihFinal());
			dataModel = DataModels.instance().getDataModel(retorno);
			dirty = false;
		} else {
			if (isAtivo() && (dirty || dataModel == null)) {
				if (isLimparPersistenceContext()) {
					getPersistenceContext().clear();
				}
				List<FatAih> list = recuperarListaPaginada(getFirstResult(), getMaxResults(), orderProperty, "asc".equalsIgnoreCase(orderDirection));
				dataModel = DataModels.instance().getDataModel(list);
				dirty = false;
			}
		}
		return dataModel;
	}*/

	@Override
	public Long recuperarCount() {
		if (getNroAihInicial() == null && getNroAihFinal() == null) {
			return faturamentoFacade.pesquisarAihsCount(getFatAih().getNroAih(), null, obterDominios(getFatAih().getIndSituacao()), getFatAih()
					.getDthrEmissao(), null, null, getFatAih().getCriadoEm());
		} else {
			return faturamentoFacade.pesquisarAihsIntervaloCount(getNroAihInicial(), getNroAihFinal());
		}
		// return Integer.MAX_VALUE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FatAih> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		List<FatAih> retorno = null;
		
		if (getNroAihInicial() == null && getNroAihFinal() == null) {
			retorno = faturamentoFacade.pesquisarAihs(firstResult, maxResult, orderProperty, asc, getFatAih().getNroAih(), null,
					obterDominios(getFatAih().getIndSituacao()), getFatAih().getDthrEmissao(), null, null, getFatAih().getCriadoEm());
		} else {
			retorno = faturamentoFacade.pesquisarAihsIntervalo(firstResult, maxResult, orderProperty, asc, getNroAihInicial(), getNroAihFinal());
		}
		
		return retorno;
	}

	private List<DominioSituacaoAih> obterDominios(DominioSituacaoAih situacaoAih) {
		List<DominioSituacaoAih> listaDominios;
		if (situacaoAih == null) {
			listaDominios = getSituacoes();
		} else {
			listaDominios = new ArrayList<DominioSituacaoAih>(1);
			listaDominios.add(situacaoAih);
		}
		return listaDominios;
	}

	public void pesquisar() {
		setCarregarInseridos(false);
		setExibirBotaoNovo(true);
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		init();
		setCarregarInseridos(false);
		this.dataModel.limparPesquisa();
	}

	public List<FatProcedimentosHospitalares> pesquisarTabela(final Object param) {
		try {
			return faturamentoFacade.listarProcedimentosHospitalar(param);
		} catch (final BaseException e) {
			LOG.error("Exceção capturada: ", e);
			return new ArrayList<FatProcedimentosHospitalares>(0);
		}
	}

	public Long pesquisarTabelaCount(final Object param) {
		try {
			return faturamentoFacade.listarProcedimentosHospitalarCount(param);
		} catch (final BaseException e) {
			LOG.error("Exceção capturada: ", e);
			return 0L;
		}
	}

	public void setInicial(final boolean inicial) {
		this.inicial = inicial;
	}

	public boolean isInicial() {
		return inicial;
	}

	public void setFatAih(FatAih fatAih) {
		this.fatAih = fatAih;
	}

	public FatAih getFatAih() {
		return fatAih;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setSituacoes(List<DominioSituacaoAih> situacoes) {
		this.situacoes = situacoes;
	}

	public List<DominioSituacaoAih> getSituacoes() {
		return situacoes;
	}

	public void setEntity(FatAih entity) {
		this.entity = entity;
	}

	public FatAih getEntity() {
		return entity;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setNroAihInicial(Long nroAihInicial) {
		this.nroAihInicial = nroAihInicial;
	}

	public Long getNroAihInicial() {
		return nroAihInicial;
	}

	public void setNroAihFinal(Long nroAihFinal) {
		this.nroAihFinal = nroAihFinal;
	}

	public Long getNroAihFinal() {
		return nroAihFinal;
	}

	public void setConfirmarGravacao(boolean confirmarGravacao) {
		this.confirmarGravacao = confirmarGravacao;
	}

	public boolean isConfirmarGravacao() {
		return confirmarGravacao;
	}

	public void setNroAihsInsercao(Long nroAihsInsercao) {
		this.nroAihsInsercao = nroAihsInsercao;
	}

	public Long getNroAihsInsercao() {
		return nroAihsInsercao;
	}

	public void setNroAihsRecusadas(Long nroAihsRecusadas) {
		this.nroAihsRecusadas = nroAihsRecusadas;
	}

	public Long getNroAihsRecusadas() {
		return nroAihsRecusadas;
	}

	public void setCarregarInseridos(boolean carregarInseridos) {
		this.carregarInseridos = carregarInseridos;
	}

	public boolean isCarregarInseridos() {
		return carregarInseridos;
	}

	public DynamicDataModel<FatAih> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatAih> dataModel) {
		this.dataModel = dataModel;
	}

}

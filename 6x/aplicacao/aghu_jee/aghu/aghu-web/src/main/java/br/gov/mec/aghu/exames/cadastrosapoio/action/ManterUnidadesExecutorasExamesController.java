package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.dominio.DominioApAnterior;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidTempoComMinuto;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterUnidadesExecutorasExamesController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -3775809947073456947L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	private AelExamesMaterialAnalise examesMaterialAnalise;

	private String sigla;

	private Integer manSeq;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private AelUnfExecutaExames aelUnfExecutaExames;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Short unfSeq;

	private AelUnfExecutaExames unfExecutaRemover;

	private List<AelUnfExecutaExames> listaAelUnfExecutaExames;
	private Boolean editando;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 

		editando = false;

		aelUnfExecutaExames = new AelUnfExecutaExames();
		aelUnfExecutaExames.setId(new AelUnfExecutaExamesId());

		if (StringUtils.isNotBlank(this.sigla) && this.manSeq != null) {
			this.examesMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);
			listaAelUnfExecutaExames = this.examesFacade.buscaListaAelUnfExecutaExames(this.sigla, this.manSeq, true);
		}

		unfSeq = null;

		setarCamposDefault();

	
	}

	private void setarCamposDefault() {

		aelUnfExecutaExames.setIndSituacao(DominioSituacao.A);
		aelUnfExecutaExames.setIndExecutaEmPlantao(false);
		aelUnfExecutaExames.setIndLiberaResultAutom(false);
		aelUnfExecutaExames.setIndExigeInfoClin(false);
		aelUnfExecutaExames.setIndAvisaSolicitante(true);
		aelUnfExecutaExames.setIndImprimeFicha(false);
		aelUnfExecutaExames.setIndPermVerLaudoExecutando(false);
		aelUnfExecutaExames.setIndImpNomeExameLaudo(true);
		aelUnfExecutaExames.setIndMonitorPendencia(false);
		aelUnfExecutaExames.setIndNroFrascoFornec(false);
		aelUnfExecutaExames.setIndImpDuasEtiq(false);
		aelUnfExecutaExames.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.N);
		aelUnfExecutaExames.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.N);
		aelUnfExecutaExames.setUnidadeMedidaTempoRealizaca(DominioUnidTempoComMinuto.D);
		aelUnfExecutaExames.setIndDesativaTemp(false);
		aelUnfExecutaExames.setIndGeraImagensPacs(DominioSimNao.N);
		aelUnfExecutaExames.setIndRecebeLaudoPacs(false);
		aelUnfExecutaExames.setIndLaudoUnico(DominioSimNao.N);
		aelUnfExecutaExames.setIndNumApAnterior(DominioApAnterior.N);

	}

	public void confirmar() {
		boolean insert = false;
		try {

			if (aelUnfExecutaExames.getId().getEmaExaSigla() == null && aelUnfExecutaExames.getId().getEmaManSeq() == null && aelUnfExecutaExames.getAelExamesMaterialAnalise() == null) {
				insert = true;
			}

			cadastrosApoioExamesFacade.persistirUnidadeExecutoraExames(aelUnfExecutaExames, examesMaterialAnalise);

			if (insert) {
				listaAelUnfExecutaExames.add(aelUnfExecutaExames);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_UNIDADE_EXECUTORA");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_UNIDADE_EXECUTORA");
				editando = false;
			}

			efetuaCargaModuloCustos(insert);

			aelUnfExecutaExames = new AelUnfExecutaExames();
			aelUnfExecutaExames.setId(new AelUnfExecutaExamesId());
			setarCamposDefault();
		} catch (BaseException e) {
			if (insert) {
				aelUnfExecutaExames.getId().setEmaExaSigla(null);
				aelUnfExecutaExames.getId().setEmaManSeq(null);
				aelUnfExecutaExames.setAelExamesMaterialAnalise(null);
			} else {
				// Desatachar aelUnfExecutaExames
				cancelarEdicao();

			}
			apresentarExcecaoNegocio(e);
		}
	}

	private void efetuaCargaModuloCustos(boolean insert) {
		try {
			if (insert && isModuloCustosAtivo()) {
				String siglaExameEdicao = examesMaterialAnalise.getAelExames().getSigla();
				String descricaoUsual = examesMaterialAnalise.getAelExames().getDescricaoUsual();

				FccCentroCustos centroCustoCCTS = null;
				if (aelUnfExecutaExames.getId().getUnfSeq().getCentroCusto() != null) {
					centroCustoCCTS = aelUnfExecutaExames.getId().getUnfSeq().getCentroCusto();
				}
				if (centroCustoCCTS != null) {
					custosSigFacade.efetuaCargaExamesComoObjetoCusto(descricaoUsual, centroCustoCCTS, siglaExameEdicao);
				}
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	private boolean isModuloCustosAtivo() {
		final String nomeModuloCusto = "sigcustosatividade";
		boolean isModuloCustosAtivo = false;
		List<Modulo> listaModulos = cascaFacade.listarModulosAtivos();
		for (Modulo modulo : listaModulos) {
			if (modulo.getNome().equalsIgnoreCase(nomeModuloCusto)) {
				isModuloCustosAtivo = true;
				break;
			}
		}
		return isModuloCustosAtivo;
	}

	public void editar(AelUnfExecutaExames aelUnfExecutaExames) {
		editando = true;
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	public void cancelarEdicao() {
		editando = false;
		// examesFacade.evict(aelUnfExecutaExames); // TODO
		this.aelUnfExecutaExames = new AelUnfExecutaExames();
		aelUnfExecutaExames.setId(new AelUnfExecutaExamesId());
		setarCamposDefault();

		listaAelUnfExecutaExames = this.examesFacade.buscaListaAelUnfExecutaExames(sigla, manSeq, true);
		this.iniciar();
	}

	public Boolean registroEditando(AelUnfExecutaExames unfExecutaGrid) {
		if(unfExecutaGrid != null){
			AelUnfExecutaExamesId idGrid = unfExecutaGrid.getId();
			if (aelUnfExecutaExames.getId() != null) {
				AelUnfExecutaExamesId idEditando = aelUnfExecutaExames.getId();
				if (idGrid.getEmaExaSigla().equals(idEditando.getEmaExaSigla()) && idGrid.getEmaManSeq().equals(idEditando.getEmaManSeq())
						&& idGrid.getUnfSeq().getSeq().equals(idEditando.getUnfSeq().getSeq()) && editando) {
					return true;
				}
			}
		}
		return false;
	}

	public void excluir() {
		try {
			this.cadastrosApoioExamesFacade.removerUnidadeExecutoraExames(this.unfExecutaRemover.getId().getEmaExaSigla(), this.unfExecutaRemover.getId().getEmaManSeq(), this.unfExecutaRemover
					.getId().getUnfSeq().getSeq());
			this.listaAelUnfExecutaExames = this.examesFacade.buscaListaAelUnfExecutaExames(this.unfExecutaRemover.getId().getEmaExaSigla(), this.unfExecutaRemover.getId().getEmaManSeq(), true);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_UNIDADE_EXECUTORA_EXAMES");
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.unfExecutaRemover = null;
		}
	}

	public boolean isDisableButtonUnidades() {
		if (this.manSeq != null && this.sigla != null && this.unfSeq != null) {
			return false;
		} else {
			return true;
		}
	}

	public void cancelarExclusao() {
		this.unfExecutaRemover = null;
	}

	public String voltar() {
		this.limparParametros();
		this.cancelarEdicao();
		this.cancelarExclusao();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	private void limparParametros() {
		this.manSeq = null;
		this.sigla = null;
		this.examesMaterialAnalise = null;
		this.aelUnfExecutaExames = null;
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.unfSeq = null;
		this.listaAelUnfExecutaExames = null;
		this.editando = null;
		this.unfExecutaRemover = null;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String param) {
		return this.returnSGWithCount(this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(param),
				this.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesCount(param));
	}
	
	public Long pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesCount(final String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesCount(param); 
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalComparece(final String param) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadesPaiPorDescricao(param),
				this.pesquisarUnidadeFuncionalComparecePorCodigoEDescricaoCount(param));
	}
	
	public Long pesquisarUnidadeFuncionalComparecePorCodigoEDescricaoCount(final String param) {
		return this.aghuFacade.pesquisarUnidadesPaiPorDescricaoCount(param);
	}
	
	public AelExamesMaterialAnalise getExamesMaterialAnalise() {
		return examesMaterialAnalise;
	}

	public void setExamesMaterialAnalise(AelExamesMaterialAnalise examesMaterialAnalise) {
		this.examesMaterialAnalise = examesMaterialAnalise;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public AelUnfExecutaExames getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}

	public void setAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	public List<AelUnfExecutaExames> getListaAelUnfExecutaExames() {
		return listaAelUnfExecutaExames;
	}

	public void setListaAelUnfExecutaExames(List<AelUnfExecutaExames> listaAelUnfExecutaExames) {
		this.listaAelUnfExecutaExames = listaAelUnfExecutaExames;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public AelUnfExecutaExames getUnfExecutaRemover() {
		return unfExecutaRemover;
	}

	public void setUnfExecutaRemover(AelUnfExecutaExames unfExecutaRemover) {
		this.unfExecutaRemover = unfExecutaRemover;
	}

}

package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioObjetoDoPac;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class EtapaPacModalidadeController extends ActionController {

	private static final String TEMPO_LOCALIZACAO_PAC_LIST = "tempoLocalizacaoPacList";

	private static final Log LOG = LogFactory.getLog(EtapaPacModalidadeController.class);

	private static final long serialVersionUID = -6876405164044629198L;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoTempoAndtPac tempoLocalizacaoPac = new ScoTempoAndtPac();

	
	private boolean visualizar;

	private ScoEtapaModPac novaEtapaModPac = new ScoEtapaModPac();

	private Integer indexEtapaEdicao;

	private List<ScoEtapaModPac> etapasModPac;

	private List<ScoEtapaModPac> etapasModPacExclusao;

	private DominioObjetoDoPac objetoPacPesquisa = DominioObjetoDoPac.M;

	private Integer tempoTotal;

	private Boolean edicaoEtapa;

	private Boolean possuiAlteracoes;

	private List<SelectItem> itemsObjetosPac;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		this.etapasModPacExclusao = new ArrayList<ScoEtapaModPac>();
		limparCampos();
		atualizaEtapas();
		this.possuiAlteracoes = Boolean.FALSE;
		popuplaItemsObjetosPac();
	}
	

	private void popuplaItemsObjetosPac() {

		itemsObjetosPac = new ArrayList<SelectItem>();

		if (objetoPacPesquisa.equals(DominioObjetoDoPac.M)) {
			itemsObjetosPac.add(new SelectItem(DominioObjetoDoPac.M.name(),
					DominioObjetoDoPac.M.getDescricao()));
			itemsObjetosPac.add(new SelectItem(DominioObjetoDoPac.M
					.getDescricao() + DominioObjetoDoPac.S.getDescricao(),
					DominioObjetoDoPac.M.getDescricao() + " e "
							+ DominioObjetoDoPac.S.getDescricao()));
		} else {
			itemsObjetosPac.add(new SelectItem(DominioObjetoDoPac.S.name(),
					DominioObjetoDoPac.S.getDescricao()));
			itemsObjetosPac.add(new SelectItem(DominioObjetoDoPac.S
					.getDescricao() + DominioObjetoDoPac.M.getDescricao(),
					DominioObjetoDoPac.S.getDescricao() + " e "
							+ DominioObjetoDoPac.M.getDescricao()));

		}

	}

	public void atualizaEtapas() {
		etapasModPac = comprasCadastrosBasicosFacade.listarEtapasPac(tempoLocalizacaoPac, objetoPacPesquisa);
		atualizaTempoTotal();
	}

	public void atualizaTempoTotal() {
		tempoTotal = 0;

		for (ScoEtapaModPac etapaModPac : etapasModPac) {
			if (etapaModPac.getNumeroDias() != null
					&& etapaModPac.getNumeroDias() > 0) {
				tempoTotal += etapaModPac.getNumeroDias();
			}
		}
	}

	public void limparCampos() {

		this.setEdicaoEtapa(Boolean.FALSE);
		this.novaEtapaModPac = new ScoEtapaModPac();
		this.novaEtapaModPac.setSituacao(DominioSituacao.A);
		this.indexEtapaEdicao = null;
	}

	public void editarEtapaPac(ScoEtapaModPac etapaModPac, Integer index) {
		try {
			this.novaEtapaModPac = (ScoEtapaModPac) etapaModPac.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe ScoEtapaModPac "
					+ "não implementa a interface Cloneable.", e);
		}
		this.indexEtapaEdicao = index;
		setEdicaoEtapa(Boolean.TRUE);
	}

	public void excluirEtapaPac(ScoEtapaModPac etapaModPac, Integer index) {

		this.etapasModPac.remove(index.intValue());

		if (etapaModPac.getCodigo() != null) {
			this.etapasModPacExclusao.add(etapaModPac);
		}

		setEdicaoEtapa(Boolean.FALSE);
		atualizaTempoTotal();

		setPossuiAlteracoes(Boolean.TRUE);
	}

	public void salvarEtapaPac() {
		if (indexEtapaEdicao != null) {
			this.etapasModPac.remove(this.indexEtapaEdicao.intValue());
		}
		etapasModPac.add(novaEtapaModPac);
		limparCampos();
		atualizaTempoTotal();
		setPossuiAlteracoes(Boolean.TRUE);
	}

	public String validarItensPendentes() {
		if (possuiAlteracoes) {
			super.openDialog("modalConfirmacaoPendenciaWG");
		} else {
			return cancelar();
		}
		return null;
	}

	public void validarItensPendentesPesquisa() {
		if (!possuiAlteracoes) {
			iniciar();
		}else{
			super.openDialog("modalConfirmacaoPendenciaPesquisaWG");
		}
	}

	public String gravar() {
		try {
			this.comprasCadastrosBasicosFacade.validaEtapasComTempoPrevistoExecucao(etapasModPac);

			for (ScoEtapaModPac etapaModPac : etapasModPac) {

				// Clone - Deve ser salvo duas etapas, mudando o objeto pac de
				// acordo com a selecao na tela
				ScoEtapaModPac etapaModPacClone = null;

				if (etapaModPac.getObjetoPac() == null
						&& etapaModPac.getDescricaoObjetoPac() != null
						&& etapaModPac.getDescricaoObjetoPac().length() == 1) {
					etapaModPac.setObjetoPac(DominioObjetoDoPac
							.valueOf(etapaModPac.getDescricaoObjetoPac()));
				} else if (etapaModPac.getObjetoPac() == null) {
					etapaModPac.setObjetoPac(DominioObjetoDoPac.S);

					try {
						etapaModPacClone = (ScoEtapaModPac) etapaModPac.clone();
					} catch (CloneNotSupportedException e) {
						LOG.error("A classe ScoEtapaModPac "
								+ "não implementa a interface Cloneable.", e);
					}

					etapaModPacClone.setObjetoPac(DominioObjetoDoPac.M);
				}

				if (etapaModPac.getVersion() == null) {
					etapaModPac.setModalidadeLicitacao(getTempoLocalizacaoPac()
							.getModalidadeLicitacao());
					etapaModPac.setLocalizacaoProcesso(getTempoLocalizacaoPac()
							.getLocalizacaoProcesso());

					this.comprasCadastrosBasicosFacade.inserirEtapaPac(etapaModPac);

					// Salva tambem o clone
					if (etapaModPacClone != null) {

						etapaModPacClone
								.setModalidadeLicitacao(getTempoLocalizacaoPac()
										.getModalidadeLicitacao());
						etapaModPacClone
								.setLocalizacaoProcesso(getTempoLocalizacaoPac()
										.getLocalizacaoProcesso());

						this.comprasCadastrosBasicosFacade.inserirEtapaPac(etapaModPacClone);
					}
				} else {

					this.comprasCadastrosBasicosFacade.alterarEtapaModPac(etapaModPac);
				}
			}

			for (ScoEtapaModPac etapaModPac : etapasModPacExclusao) {
				this.comprasCadastrosBasicosFacade.excluirEtapaModPac(etapaModPac);
			}

			apresentarMsgNegocio(
					Severity.INFO,
					"MENSAGE_SUCESSO_CADASTRO_ETAPAS_PAC",
					tempoLocalizacaoPac.getModalidadeLicitacao().getCodigo()
							+ "-"
							+ tempoLocalizacaoPac.getModalidadeLicitacao()
									.getDescricao(),
					tempoLocalizacaoPac.getLocalizacaoProcesso().getCodigo()
							+ "-"
							+ tempoLocalizacaoPac.getLocalizacaoProcesso()
									.getDescricao(),
					objetoPacPesquisa.getDescricao());

			return TEMPO_LOCALIZACAO_PAC_LIST;

		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		return TEMPO_LOCALIZACAO_PAC_LIST;
	}

	public ScoTempoAndtPac getTempoLocalizacaoPac() {
		return tempoLocalizacaoPac;
	}

	public void setTempoLocalizacaoPac(ScoTempoAndtPac tempoLocalizacaoPac) {
		this.tempoLocalizacaoPac = tempoLocalizacaoPac;
	}

	// Métodos para carregar suggestion
	// Modalidade Licitação - Ativas
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(
			Object modalidade) {
		return this.comprasCadastrosBasicosFacade
				.listarModalidadeLicitacaoAtivas(modalidade);
	}

	// Localização Processo - Ativos
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcesso(
			Object localizacao) {
		return this.comprasCadastrosBasicosFacade
				.pesquisarLocalizacaoProcessoPorCodigoOuDescricao(localizacao,
						true);
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public ScoEtapaModPac getNovaEtapaModPac() {
		return novaEtapaModPac;
	}

	public void setNovaEtapaModPac(ScoEtapaModPac novaEtapaModPac) {
		this.novaEtapaModPac = novaEtapaModPac;
	}

	public List<ScoEtapaModPac> getEtapasModPac() {
		return etapasModPac;
	}

	public void setEtapasModPac(List<ScoEtapaModPac> etapasModPac) {
		this.etapasModPac = etapasModPac;
	}

	public Integer getTempoTotal() {
		return tempoTotal;
	}

	public void setTempoTotal(Integer tempoTotal) {
		this.tempoTotal = tempoTotal;
	}

	public Boolean getEdicaoEtapa() {
		return edicaoEtapa;
	}

	public void setEdicaoEtapa(Boolean edicaoEtapa) {
		this.edicaoEtapa = edicaoEtapa;
	}

	public List<ScoEtapaModPac> getEtapasModPacExclusao() {
		return etapasModPacExclusao;
	}

	public void setEtapasModPacExclusao(
			List<ScoEtapaModPac> etapasModPacExclusao) {
		this.etapasModPacExclusao = etapasModPacExclusao;
	}

	public Boolean getPossuiAlteracoes() {
		return possuiAlteracoes;
	}

	public void setPossuiAlteracoes(Boolean possuiAlteracoes) {
		this.possuiAlteracoes = possuiAlteracoes;
	}

	public List<SelectItem> getItemsObjetosPac() {
		return itemsObjetosPac;
	}

	public void setItemsObjetosPac(List<SelectItem> itemsObjetosPac) {
		this.itemsObjetosPac = itemsObjetosPac;
	}

	public DominioObjetoDoPac getObjetoPacPesquisa() {
		return objetoPacPesquisa;
	}

	public void setObjetoPacPesquisa(DominioObjetoDoPac objetoPacPesquisa) {
		this.objetoPacPesquisa = objetoPacPesquisa;
	}
}
package br.gov.mec.aghu.suprimentos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.parecer.action.ParecerController;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMarcaModeloId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterMarcaComercialController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ScoMarcaModelo> dataModel;

    @Inject
    private ParecerController parecerController;

	private static final Log LOG = LogFactory.getLog(ManterMarcaComercialController.class);

	private static final long serialVersionUID = 7918975657568862750L;

	@EJB
	private IComprasFacade comprasFacade;

	private Integer seqp;
	private Integer codigo;

	private Boolean visivel = false;
	private Boolean visivelNovo = false;
	private Boolean retornarModelo = false;

	private ScoMarcaComercial marcaComercial = new ScoMarcaComercial();
	private ScoMarcaModelo marcaModelo = new ScoMarcaModelo();

	private String voltarParaUrl;

	//TODO MIGRAÇÃO @Out(required=false)
	private ScoMarcaComercial marcaComercialInserida;

	//TODO MIGRAÇÃO @Out(required=false)
	private ScoMarcaModelo marcaModeloInserida;

	public void iniciar() {
	 


		if (codigo != null) {
			this.visivelNovo = true;
			this.marcaComercial = comprasFacade.buscaScoMarcaComercialPorId(codigo);
			this.marcaModelo.setId(new ScoMarcaModeloId());
			if (seqp != null) {
				this.marcaModelo = this.comprasFacade.buscaScoMarcaModeloPorId(seqp, codigo);
				this.visivel = true;
			} else {
				this.getDataModel().setPesquisaAtiva(false);
			}
			if (recuperarCount() != 0) {
				this.getDataModel().reiniciarPaginator();
			}
		} else {
			this.visivelNovo = false;
			marcaComercial.setIndSituacao(DominioSituacao.A);
			marcaModelo.setIndSituacao(DominioSituacao.A);
		}
		this.marcaComercialInserida = null;
	
	}

	/**
	 * Grava Marca Comercial
	 */
	public void gravarMarcaComercial() {
		try {
			this.comprasFacade.persistirScoMarcaComercial(this.marcaComercial);
			if (this.codigo == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_MARCA_COMERCIAL", this.marcaComercial.getDescricao());
				this.visivelNovo = true;
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_MARCA_COMERCIAL", this.marcaComercial.getDescricao());
			}

			// Injeta marca comercial somente quando ativa.
			if (DominioSituacao.A.equals(marcaComercial.getIndSituacao())) {
				try {
					this.marcaComercialInserida = (ScoMarcaComercial) BeanUtils.cloneBean(this.marcaComercial);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				// Se marca comercial alterada para inativa, desinjeta-a.
			} else if (marcaComercial.equals(marcaComercialInserida)) {
				marcaComercialInserida = null;
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Grava Marca Modelo
	 */
	public String gravarMarcaModelo() {
		this.marcaModelo.setId(new ScoMarcaModeloId());
		if (this.marcaComercial.getCodigo() != null) {
			this.marcaModelo.getId().setMcmCodigo(this.marcaComercial.getCodigo());
			this.marcaModelo.getId().setSeqp(this.seqp);

			try {
				this.comprasFacade.persistirScoMarcaModelo(this.marcaModelo, this.marcaComercial);
			} catch (ApplicationBusinessException e1) {
			    this.apresentarExcecaoNegocio(e1);
				this.codigo = null;
				this.seqp = null;
				this.marcaModelo = new ScoMarcaModelo();
				this.marcaModelo.setId(new ScoMarcaModeloId());
			    return null;
			}
			
			if (this.retornarModelo != null && this.retornarModelo) {
                parecerController.setCameFromMarcaModeloCrud(true);
                this.setMarcaModeloInserida(marcaModelo);
				return voltarParaUrl;
			}
			if (this.seqp == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_MARCA_MODELO", this.marcaModelo.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_MARCA_MODELO", this.marcaModelo.getDescricao());
				this.visivel = false;
			}

			// Injeta modelo somente quando ativo
			if (DominioSituacao.A.equals(marcaModelo.getIndSituacao())) {
				try {
					this.setMarcaModeloInserida((ScoMarcaModelo) BeanUtils.cloneBean(this.marcaModelo));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				// Se modelo alterado para inativo, desinjeta-o.
			} else if (marcaModelo.equals(marcaModeloInserida)) {
				marcaModeloInserida = null;
			}

			this.getDataModel().reiniciarPaginator();
		} else {
			apresentarMsgNegocio(Severity.INFO, "ERRO_GRAVAR_MARCA_MODELO");
		}
		this.codigo = null;
		this.seqp = null;
		this.marcaModelo = new ScoMarcaModelo();
		this.marcaModelo.setId(new ScoMarcaModeloId());
		this.marcaModelo.setIndSituacao(DominioSituacao.A);
		return null;
	}

	public String cancelar() {
		this.codigo = null;
		this.seqp = null;
		this.marcaComercial = new ScoMarcaComercial();
		this.marcaModelo = new ScoMarcaModelo();
		this.getDataModel().limparPesquisa();
		final String retorno = this.voltarParaUrl;
		this.voltarParaUrl = null;
		this.visivel = false;
		return retorno;
	}

	public void editar(ScoMarcaModelo item) {
		this.marcaModelo = item;
		this.marcaModelo.setDescricao(item.getDescricao());
		this.codigo = item.getId().getMcmCodigo();
		this.seqp = item.getId().getSeqp();
		this.visivel = true;
	}

	public void cancelarEdicao() {
		this.seqp = null;
		this.marcaModelo = new ScoMarcaModelo();
		this.marcaModelo.setIndSituacao(DominioSituacao.A);
		this.visivel = false;
	}

	@SuppressWarnings("rawtypes")
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<ScoMarcaModelo> result = null;
		if (marcaComercial.getCodigo() != null) {
			result = this.comprasFacade.listaMarcaModelo(firstResult, maxResult, orderProperty, true, this.marcaComercial.getCodigo());
		}
		if (result == null) {
			result = new ArrayList<ScoMarcaModelo>();
		}
		return result;
	}

	public Long recuperarCount() {
		if (marcaComercial.getCodigo() != null) {
			return comprasFacade.pesquisarMarcaModeloCount(this.marcaComercial.getCodigo());
		} else {
			return 0L;
		}
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public ScoMarcaModelo getMarcaModelo() {
		return marcaModelo;
	}

	public void setMarcaModelo(ScoMarcaModelo marcaModelo) {
		this.marcaModelo = marcaModelo;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}

	public Boolean getVisivelNovo() {
		return visivelNovo;
	}

	public void setVisivelNovo(Boolean visivelNovo) {
		this.visivelNovo = visivelNovo;
	}

	public ScoMarcaComercial getMarcaComercialInserida() {
		return marcaComercialInserida;
	}

	public void setMarcaComercialInserida(ScoMarcaComercial marcaComercialInserida) {
		this.marcaComercialInserida = marcaComercialInserida;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public ScoMarcaModelo getMarcaModeloInserida() {
		return marcaModeloInserida;
	}

	public void setMarcaModeloInserida(ScoMarcaModelo marcaModeloInserida) {
		this.marcaModeloInserida = marcaModeloInserida;
	}

	public void setRetornarModelo(Boolean retornarModelo) {
		this.retornarModelo = retornarModelo;
	}

	public Boolean getRetornarModelo() {
		return retornarModelo;
	}

	public DynamicDataModel<ScoMarcaModelo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMarcaModelo> dataModel) {
		this.dataModel = dataModel;
	}
}
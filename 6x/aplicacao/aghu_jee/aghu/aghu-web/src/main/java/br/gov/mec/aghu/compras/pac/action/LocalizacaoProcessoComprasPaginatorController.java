package br.gov.mec.aghu.compras.pac.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.suprimentos.vo.ScoLocalizacaoProcessoComprasVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller de Localização de Processos de Compra
 * 
 * @author rpanassolo
 */

public class LocalizacaoProcessoComprasPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ScoLocalizacaoProcessoComprasVO> dataModel;

	private static final Log LOG = LogFactory.getLog(LocalizacaoProcessoComprasPaginatorController.class);
	private static final long serialVersionUID = -5774965552335019985L;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;

	private ScoLocalizacaoProcesso local;

	private Integer protocolo;

	private Integer nroPac;

	private Short complemento;

	private ScoModalidadeLicitacao modalidadeCompra;

	private Integer nroAF;

	private Date dtEntrada;

	private RapServidores servidorResponsavel;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Limpa.
	 */
	public void limpar() {
		protocolo = null;
		nroPac = null;
		complemento = null;
		modalidadeCompra = null;
		servidorResponsavel = null;
		nroAF = null;
		dtEntrada = null;
		local = null;
		this.dataModel.setPesquisaAtiva(false);
	}

	public void iniciar() {
	 

	 

		this.dataModel.setPesquisaAtiva(false);
	
	}
	

	@Override
	public Long recuperarCount() {
		return pacFacade.pesquisarLocalizacaoProcessoCompraCount(protocolo, local, nroPac, complemento, modalidadeCompra, nroAF, dtEntrada,
				servidorResponsavel);
	}

	@Override
	public List<ScoLocalizacaoProcessoComprasVO> recuperarListaPaginada(Integer first, Integer max, String order, boolean asc) {
		return pacFacade.pesquisarLocalizacaoProcessoCompra(first, max, order, asc, protocolo, local, nroPac, complemento,
				modalidadeCompra, nroAF, dtEntrada, servidorResponsavel);
	}

	public void pesquisar() {
		if (validarCamposObrigatorios()) {
			this.dataModel.reiniciarPaginator();
		}
	}

	private Boolean validarCamposObrigatorios() {
		Boolean validacao = Boolean.FALSE;
		try {
			validacao = pacFacade.validarCamposObrigatorioLocalizacao(protocolo, local, nroPac, complemento, modalidadeCompra, nroAF,
					dtEntrada, servidorResponsavel);
		} catch (ApplicationBusinessException e) {
			LOG.error(e, e.getCause());
			apresentarExcecaoNegocio(e);
		}
		return validacao;
	}

	public List<ScoLocalizacaoProcesso> pesquisarLocalizacoes(String filter) {
		return this.returnSGWithCount(comprasCadastrosBasicosFacade.pesquisarLocalizacaoProcessoPorCodigoOuDescricaoOrderByDescricao(filter, Boolean.FALSE),pesquisarLocalizacoesCount(filter));
	}

	public Long pesquisarLocalizacoesCount(String filter) {
		return comprasCadastrosBasicosFacade.pesquisarLocalizacaoProcessoPorCodigoOuDescricaoCount(filter, Boolean.FALSE);
	}

	// suggestions
	public List<ScoModalidadeLicitacao> pesquisarModalidades(String filter) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(filter),pesquisarModalidadesCount(filter));
	}

	public Long pesquisarModalidadesCount(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivasCount(filter);
	}

	public List<RapServidores> pesquisarServidorPorMatriculaOuNome(String parametro) {
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidorPorMatriculaNome(parametro),pesquisarServidorPorMatriculaOuNomeCount(parametro));
	}

	public Long pesquisarServidorPorMatriculaOuNomeCount(String parametro) {
		return registroColaboradorFacade.pesquisarServidorPorMatriculaNomeCount(parametro);
	}

	public String abreviar(String str, int maxWidth) {
		String abreviado = null;
		if (str != null) {
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}

	// Getters/Setters
	public ScoLocalizacaoProcesso getLocal() {
		return local;
	}

	public void setLocal(ScoLocalizacaoProcesso local) {
		this.local = local;
	}

	public Integer getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Integer protocolo) {
		this.protocolo = protocolo;
	}

	public Integer getNroPac() {
		return nroPac;
	}

	public void setNroPac(Integer nroPac) {
		this.nroPac = nroPac;
	}

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}

	public void setServidorResponsavel(RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public DynamicDataModel<ScoLocalizacaoProcessoComprasVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLocalizacaoProcessoComprasVO> dataModel) {
		this.dataModel = dataModel;
	}
}

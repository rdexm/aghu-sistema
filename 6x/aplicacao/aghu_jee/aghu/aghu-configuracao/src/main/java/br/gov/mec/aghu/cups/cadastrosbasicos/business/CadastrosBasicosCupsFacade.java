package br.gov.mec.aghu.cups.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.dao.ImpComputadorDAO;
import br.gov.mec.aghu.configuracao.dao.ImpComputadorImpressoraDAO;
import br.gov.mec.aghu.configuracao.dao.ImpImpressoraDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.dominio.DominioTipoImpressoraCups;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.model.cups.ImpServidorCups;

/**
 * Porta de entrada do módulo do CUPS.
 * 
 * @author riccosta
 */


@Modulo(ModuloEnum.CONFIGURACAO)
@Stateless
public class CadastrosBasicosCupsFacade extends BaseFacade implements ICadastrosBasicosCupsFacade {

@EJB
private ServidorCupsON servidorCupsON;
@EJB
private ImpressoraON impressoraON;
@EJB
private ClasseImpressaoON classeImpressaoON;
@EJB
private ComputadorON computadorON;
@EJB
private ComputadorImpressoraON computadorImpressoraON;

@Inject
private ImpComputadorImpressoraDAO impComputadorImpressoraDAO;

@Inject
private ImpComputadorDAO impComputadorDAO;

@Inject
private ImpImpressoraDAO impImpressoraDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8897055061090939123L;

	protected ClasseImpressaoON getClasseImpressaoON() {
		return classeImpressaoON;
	}

	protected ComputadorImpressoraON getComputadorImpressoraON() {
		return computadorImpressoraON;
	}

	protected ComputadorON getComputadorON() {
		return computadorON;
	}

	protected ImpressoraON getImpressoraON() {
		return impressoraON;
	}

	protected ServidorCupsON getServidorCupsON() {
		return servidorCupsON;
	}

	@Override
	public List<ImpClasseImpressao> pesquisarClasseImpressao(
			Object paramPesquisa) {
		return getClasseImpressaoON().pesquisarClasseImpressao(paramPesquisa);
	}

	@Override
	public List<ImpComputador> pesquisarComputador(Object paramPesquisa) {
		return getComputadorON().pesquisarComputador(paramPesquisa);
	}

	@Override
	public List<ImpImpressora> pesquisarImpressoraPorFila(Object paramPesquisa) {
		return getImpressoraON().pesquisarImpressoraPorFila(paramPesquisa);
	}

	@Override
	public List<ImpServidorCups> pesquisarServidorCups(Object paramPesquisa) {
		return getServidorCupsON().pesquisarServidorCups(paramPesquisa);
	}

	@Override
	public Long pesquisarClasseImpressaoCount(String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups) {
		return getClasseImpressaoON().pesquisarClasseImpressaoCount(
				classeImpressao, tipoImpressora, descricao, tipoCups);
	}

	@Override
	public List<ImpClasseImpressao> pesquisarClasseImpressao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups) {
		return getClasseImpressaoON().pesquisarClasseImpressao(firstResult,
				maxResult, orderProperty, asc, classeImpressao, tipoImpressora,
				descricao, tipoCups);
	}

	@Override
	public ImpClasseImpressao obterClasseImpressao(Integer idClasseImpressao)
			throws ApplicationBusinessException {
		return getClasseImpressaoON().obterClasseImpressao(idClasseImpressao);
	}

	@Override
	public void gravarClasseImpressao(ImpClasseImpressao impClasseImpressao)
			throws ApplicationBusinessException {
		getClasseImpressaoON().gravarClasseImpressao(impClasseImpressao);
	}

	@Override
	public void excluirClasseImpressao(Integer idClasseImpressao)
			throws ApplicationBusinessException {
		getClasseImpressaoON().excluirClasseImpressao(idClasseImpressao);
	}

	@Override
	public ImpComputador obterComputador(Integer idComputador)
			throws ApplicationBusinessException {
		return getComputadorON().obterComputador(idComputador);
	}

	@Override
	public void gravarComputador(ImpComputador impComputador)
			throws ApplicationBusinessException {
		getComputadorON().gravarComputador(impComputador);
	}

	@Override
	public void excluirComputador(Integer idComputador)
			throws ApplicationBusinessException {
		getComputadorON().excluirComputador(idComputador);
	}

	@Override
	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa,  boolean soCodBarras) {
		return getImpressoraON().pesquisarImpressora(paramPesquisa,  soCodBarras);
	}
	
	@Override
	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa) {
		return getImpressoraON().pesquisarImpressora(paramPesquisa);
	}

	@Override
	public void excluirComputadorImpressora(Integer idComputadorImpressora)
			throws ApplicationBusinessException {
		getComputadorImpressoraON().excluirComputadorImpressora(
				idComputadorImpressora);
	}

	@Override
	public ImpComputadorImpressora obterComputadorImpressora(
			Integer idComputadorImpressora) throws ApplicationBusinessException {
		return getComputadorImpressoraON().obterComputadorImpressora(
				idComputadorImpressora);
	}

	@Override
	public void gravarComputadorImpressora(
			ImpComputadorImpressora impComputadorImpressora)
			throws ApplicationBusinessException {
		getComputadorImpressoraON().gravarComputadorImpressora(
				impComputadorImpressora);
	}

	@Override
	public Long pesquisarComputadorImpressoraCount(
			ImpComputadorImpressora impComputadorImpressora) {
		return getComputadorImpressoraON().pesquisarComputadorImpressoraCount(
				impComputadorImpressora);
	}

	@Override
	public List<ImpComputadorImpressora> pesquisarComputadorImpressora(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ImpComputadorImpressora impComputadorImpressora) {
		return getComputadorImpressoraON().pesquisarComputadorImpressora(
				firstResult, maxResult, orderProperty, asc,
				impComputadorImpressora);
	}

	@Override
	public Long pesquisarComputadorCount(String ipComputador,
			String nomeComputador, String descricao) {
		return getComputadorON().pesquisarComputadorCount(ipComputador,
				nomeComputador, descricao);
	}

	@Override
	public List<ImpComputador> pesquisarComputador(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String ipComputador, String nomeComputador, String descricao) {
		return getComputadorON().pesquisarComputador(firstResult, maxResult,
				orderProperty, asc, ipComputador, nomeComputador, descricao);
	}

	@Override
	public ImpImpressora obterImpressora(Integer idImpressora)
			throws ApplicationBusinessException {
		return getImpressoraON().obterImpressora(idImpressora);
	}

	@Override
	public void gravarImpressora(ImpImpressora impImpressora,
			DominioTipoImpressoraCups tipoImpressoraAnt)
			throws ApplicationBusinessException {
		getImpressoraON().gravarImpressora(impImpressora, tipoImpressoraAnt);

	}

	@Override
	public void excluirImpressora(Integer idImpressora)
			throws ApplicationBusinessException {
		getImpressoraON().excluirImpressora(idImpressora);
	}

	@Override
	public List<ImpImpressora> obterImpressoraRedirecionamento(
			ImpImpressora impImpressora, String strPesquisa) {
		return getImpressoraON().obterImpressoraRedirecionamento(impImpressora,
				strPesquisa);
	}
	

	@Override
	public ImpImpressora obterImpImpressoraById(Integer id){
		return this.impImpressoraDAO.obterPorChavePrimaria(id);
	}

	
	@Override
	public Long pesquisarImpressoraCount(String filaImpressora,
			DominioTipoImpressoraCups tipoImpressora, String descricao) {
		return getImpressoraON().pesquisarImpressoraCount(filaImpressora,
				tipoImpressora, descricao);
	}

	@Override
	public List<ImpImpressora> pesquisarImpressora(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String filaImpressora, DominioTipoImpressoraCups tipoImpressora,
			String descricao) {
		return getImpressoraON().pesquisarImpressora(firstResult, maxResult,
				orderProperty, asc, filaImpressora, tipoImpressora, descricao);
	}

	@Override
	public void excluirServidorCups(Integer idServidorCups)
			throws ApplicationBusinessException {
		getServidorCupsON().excluirServidorCups(idServidorCups);
	}

	@Override
	public void gravarServidorCups(ImpServidorCups impServidorCups)
			throws ApplicationBusinessException {
		getServidorCupsON().gravarServidorCups(impServidorCups);
	}

	@Override
	public ImpServidorCups obterServidorCups(Integer idServidorCups)
			throws ApplicationBusinessException {
		return getServidorCupsON().obterServidorCups(idServidorCups);
	}

	@Override
	public Long pesquisarServidorCupsCount(String ipServidor,
			String nomeCups, String descricao) {
		return getServidorCupsON().pesquisarServidorCupsCount(ipServidor,
				nomeCups, descricao);
	}

	@Override
	public List<ImpServidorCups> pesquisarServidorCups(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String ipServidor, String nomeCups, String descricao) {
		return getServidorCupsON().pesquisarServidorCups(firstResult,
				maxResult, orderProperty, asc, ipServidor, nomeCups, descricao);
	}

	@Override
	public void validarIp(String ip) throws ApplicationBusinessException {
		this.getComputadorON().validarIp(ip);
	}

	/**
	 * Retorna computador pelo IP fornecido.
	 * 
	 * @param ipComputador
	 * @return
	 */
	@Override
	public ImpComputador obterComputador(String ipComputador) {
		return this.getImpComputadorDAO().buscarComputador(ipComputador);
	}

	private ImpComputadorDAO getImpComputadorDAO() {
		return impComputadorDAO;
	}

	@Override
	public ImpComputadorImpressora obterImpressora(Integer idComputador,
			DominioTipoCups tipo) {
		return this.getImpComputadorImpressoraDAO().buscarImpressora(
				idComputador, tipo);
	}

	@Override
	public Long pesquisarComputadorImpressoraCount(String ipAddress) {
		return this.getImpComputadorImpressoraDAO()
				.pesquisarComputadorImpressoraCount(ipAddress);
	}

	@Override
	public List<ImpComputadorImpressora> pesquisarComputadorImpressora(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String ipAddress) {
		return this.getImpComputadorImpressoraDAO().pesquisarComputadorImpressora(
				firstResult, maxResult, orderProperty, asc, ipAddress);
	}

	private ImpComputadorImpressoraDAO getImpComputadorImpressoraDAO() {
		return impComputadorImpressoraDAO;
	}

	@Override
	public ImpComputador obterComputadorPorNome(String nomePc) {
		return impComputadorDAO.obterComputadorPorNome(nomePc);
	}

}

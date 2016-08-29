package br.gov.mec.aghu.cups.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.dominio.DominioTipoImpressoraCups;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.model.cups.ImpServidorCups;

public interface ICadastrosBasicosCupsFacade extends Serializable {

	public List<ImpClasseImpressao> pesquisarClasseImpressao(
			Object paramPesquisa);

	public List<ImpComputador> pesquisarComputador(Object paramPesquisa);

	public List<ImpImpressora> pesquisarImpressoraPorFila(Object paramPesquisa);

	public List<ImpServidorCups> pesquisarServidorCups(Object paramPesquisa);

	public Long pesquisarClasseImpressaoCount(String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups);

	public List<ImpClasseImpressao> pesquisarClasseImpressao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups);

	public ImpClasseImpressao obterClasseImpressao(Integer idClasseImpressao)
			throws ApplicationBusinessException;

	public void gravarClasseImpressao(ImpClasseImpressao impClasseImpressao)
			throws ApplicationBusinessException;

	public void excluirClasseImpressao(Integer idClasseImpressao)
			throws ApplicationBusinessException;

	public ImpComputador obterComputador(Integer idComputador)
			throws ApplicationBusinessException;

	public void gravarComputador(ImpComputador impComputador)
			throws ApplicationBusinessException;

	public void excluirComputador(Integer idComputador)
			throws ApplicationBusinessException;

	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa,
			boolean soCodBarras);

	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa);

	public void excluirComputadorImpressora(Integer idComputadorImpressora)
			throws ApplicationBusinessException;

	public ImpComputadorImpressora obterComputadorImpressora(
			Integer idComputadorImpressora) throws ApplicationBusinessException;

	public void gravarComputadorImpressora(
			ImpComputadorImpressora impComputadorImpressora)
			throws ApplicationBusinessException;

	public Long pesquisarComputadorImpressoraCount(
			ImpComputadorImpressora impComputadorImpressora);

	public List<ImpComputadorImpressora> pesquisarComputadorImpressora(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ImpComputadorImpressora impComputadorImpressora);

	public Long pesquisarComputadorCount(String ipComputador,
			String nomeComputador, String descricao);

	public List<ImpComputador> pesquisarComputador(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String ipComputador, String nomeComputador, String descricao);

	public ImpImpressora obterImpressora(Integer idImpressora)
			throws ApplicationBusinessException;

	public void gravarImpressora(ImpImpressora impImpressora,
			DominioTipoImpressoraCups tipoImpressoraAnt)
			throws ApplicationBusinessException;

	public void excluirImpressora(Integer idImpressora)
			throws ApplicationBusinessException;

	public List<ImpImpressora> obterImpressoraRedirecionamento (
			ImpImpressora impImpressora, String strPesquisa);
	
	public ImpImpressora obterImpImpressoraById(Integer id);

	public Long pesquisarImpressoraCount(String filaImpressora,
			DominioTipoImpressoraCups tipoImpressora, String descricao);

	public List<ImpImpressora> pesquisarImpressora(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String filaImpressora, DominioTipoImpressoraCups tipoImpressora,
			String descricao);

	public void excluirServidorCups(Integer idServidorCups)
			throws ApplicationBusinessException;

	public void gravarServidorCups(ImpServidorCups impServidorCups)
			throws ApplicationBusinessException;

	public ImpServidorCups obterServidorCups(Integer idServidorCups)
			throws ApplicationBusinessException;

	public Long pesquisarServidorCupsCount(String ipServidor,
			String nomeCups, String descricao);

	public List<ImpServidorCups> pesquisarServidorCups(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String ipServidor, String nomeCups, String descricao);

	public void validarIp(String ip) throws ApplicationBusinessException;

	/**
	 * Retorna computador pelo IP fornecido.
	 * 
	 * @param ipComputador
	 * @return
	 */
	public ImpComputador obterComputador(String ipComputador);

	public ImpComputadorImpressora obterImpressora(Integer idComputador,
			DominioTipoCups tipo);

	public Long pesquisarComputadorImpressoraCount(String ipAddress);

	public List<ImpComputadorImpressora> pesquisarComputadorImpressora(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String ipAddress);

	public ImpComputador obterComputadorPorNome(String remoteHost);

}
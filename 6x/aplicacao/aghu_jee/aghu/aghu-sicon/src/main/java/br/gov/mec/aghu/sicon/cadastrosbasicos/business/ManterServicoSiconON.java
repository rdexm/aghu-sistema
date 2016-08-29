package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ManterMaterialSiconON.ManterMaterialSiconONExceptionCode;
import br.gov.mec.aghu.sicon.dao.ScoServicoSiconDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterServicoSiconON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterServicoSiconON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoServicoSiconDAO scoServicoSiconDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5303344102364141121L;


	public enum ManterRelacionamentoServicoSiconONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SERVICO_SICON_OBRIGATORIO, MENSAGEM_CODIGO_SERVICO_SICON_DUPLICADO, MENSAGEM_SERVICO_SICON_DUPLICADO;
	}

	public List<ScoServico> listarServicosAtivos(Object pesquisa) throws BaseException {
		return comprasFacade.listarServicosAtivos(pesquisa);		
	}
	
	public Long listarServicosAtivosCount(Object pesquisa) throws BaseException {
		return comprasFacade.listarServicosAtivosCount(pesquisa);		
	}
	
	public List<ScoServico> listarServicosDeGruposAtivos(Object pesquisa, ScoGrupoServico grupoSrv) throws BaseException {

		return comprasFacade.listarServicosByNomeOrCodigoGrupoAtivo(pesquisa, grupoSrv);
	}
	
	public Long listarServicosDeGruposAtivosCount(Object pesquisa, ScoGrupoServico grupoSrv) throws BaseException {

		return comprasFacade.listarServicosByNomeOrCodigoGrupoAtivoCount(pesquisa, grupoSrv);
	}
	
	public List<ScoServicoSicon> pesquisarServicoSicon(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoSicon, ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico) {
		return this.getScoServicoSiconDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, codigoSicon, servico, situacao,
				grupoServico);
	}

	public Long pesquisarServicoSiconCount(Integer codigoSicon,
			ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico) {

		return this.getScoServicoSiconDAO().pesquisarServicoSiconCount(
				codigoSicon, servico, situacao, grupoServico);
	}

	public void alterar(ScoServicoSicon scoServicoSicon)
			throws ApplicationBusinessException {

		validaServicoSicon(scoServicoSicon);

		if (scoServicoSicon.getSituacao() == DominioSituacao.A) {
			// RN 2
			validarCodigoSiconServicoUnico(scoServicoSicon, 2);
		}

		scoServicoSicon.setAlteradoEm(new Date());

		ScoServicoSiconDAO scoServicoSiconDAO = this.getScoServicoSiconDAO();
		scoServicoSiconDAO.atualizar(scoServicoSicon);
		scoServicoSiconDAO.flush();
	}

	public void inserir(ScoServicoSicon scoServicoSicon)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		validaServicoSicon(scoServicoSicon);

		// RN 2
		validarCodigoSiconServicoUnico(scoServicoSicon, 2);

		scoServicoSicon.setCriadoEm(new Date());
		scoServicoSicon.setServidor(servidorLogado);
		try {
			ScoServicoSiconDAO scoServicoSiconDAO = this.getScoServicoSiconDAO();
			scoServicoSiconDAO.persistir(scoServicoSicon);
			scoServicoSiconDAO.flush();
		} catch (ConstraintViolationException ise) {
			String mensagem = " Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterMaterialSiconONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);
		}
	}

	// Regras de Negócio.
	public void validaServicoSicon(ScoServicoSicon scoServicoSicon)
			throws ApplicationBusinessException {
		if (scoServicoSicon == null) {
			throw new ApplicationBusinessException(
					ManterRelacionamentoServicoSiconONExceptionCode.MENSAGEM_SERVICO_SICON_OBRIGATORIO);
		}
	}

	public void validarCodigoSiconServicoUnico(ScoServicoSicon scoServicoSicon,
			Integer rn) throws ApplicationBusinessException {

		Long numCodServicos = 0l;

		// RN 1
		if (rn == 1) {
			numCodServicos = this.getScoServicoSiconDAO()
					.validarCodigoSiconServicoUnico(scoServicoSicon.getSeq(),
							scoServicoSicon.getCodigoSicon(), null);

			if (numCodServicos > 0) {
				throw new ApplicationBusinessException(
						ManterRelacionamentoServicoSiconONExceptionCode.MENSAGEM_CODIGO_SERVICO_SICON_DUPLICADO);
			}
		} else {
			// RN 2
			if (rn == 2) {
				numCodServicos = this.getScoServicoSiconDAO()
						.validarCodigoSiconServicoUnico(
								scoServicoSicon.getSeq(), null,
								scoServicoSicon.getServico());

				if (numCodServicos > 0) {
					throw new ApplicationBusinessException(
							ManterRelacionamentoServicoSiconONExceptionCode.MENSAGEM_SERVICO_SICON_DUPLICADO);
				}
			}
		}
	}

	protected ScoServicoSiconDAO getScoServicoSiconDAO() {
		return scoServicoSiconDAO;
	}

	// Facades

	protected IComprasFacade getComprasFacade(){
		return comprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
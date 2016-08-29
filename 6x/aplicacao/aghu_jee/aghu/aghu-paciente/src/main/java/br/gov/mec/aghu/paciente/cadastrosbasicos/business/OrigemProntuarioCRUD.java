package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghSamisJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AghSamisDAO;
import br.gov.mec.aghu.paciente.dao.AghSamisJnDAO;

@Stateless
public class OrigemProntuarioCRUD extends BaseBusiness {

	private static final long serialVersionUID = -6660999257099967893L;

	private static final Log LOG = LogFactory.getLog(OrigemProntuarioCRUD.class);

	@Inject
	private AghSamisDAO aghSamisDAO;
	
	@Inject
	private AghSamisJnDAO aghSamisJnDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private enum OrigemProntuarioCRUDExceptionCode implements
	BusinessExceptionCode {
		ERRO_REMOVER_ORIGEM_PRONTUARIO;
	}

	public void persistirOrigemProntuario(AghSamis samisOrigemProntuario,
			RapServidores servidorLogado) throws ApplicationBusinessException {

		if (samisOrigemProntuario.getCodigo() == null) {
			// inclusão
			samisOrigemProntuario.setServidor(servidorLogado);
			samisOrigemProntuario.setCriadoEm(new Date());
			this.incluirOrigemProntuario(samisOrigemProntuario);
		} else {
			// edição

			AghSamisJn samisJn = populaAghSamisJn(
					samisOrigemProntuario.getCodigo(), servidorLogado, "UPD");

			this.incluirSamisJn(samisJn);

			this.atualizarOrigemProntuario(samisOrigemProntuario);
		}

	}

	private AghSamisJn populaAghSamisJn(Short codigoAghSamisAntigo,
			RapServidores servidorLogado, String operacao) {
		AghSamisJn aghSamisJn = new AghSamisJn();

		AghSamis aghSamisAntigo = getAghSamisDAO().obterOriginal(
				codigoAghSamisAntigo);

		aghSamisJn.setServidorJn(servidorLogado.getUsuario());
		aghSamisJn.setDataAlteracaoExclusao(new Date());
		aghSamisJn.setOperacaoJn(operacao);
		aghSamisJn.setCodigo(aghSamisAntigo.getCodigo());
		aghSamisJn.setDescricao(aghSamisAntigo.getDescricao());
		aghSamisJn.setIndAtivo(aghSamisAntigo.getIndAtivo());
		aghSamisJn.setCriadoEm(aghSamisAntigo.getCriadoEm());
		aghSamisJn.setSerMatricula(aghSamisAntigo.getServidor().getId()
				.getMatricula());
		aghSamisJn.setSerVinCodigo(aghSamisAntigo.getServidor().getId()
				.getVinCodigo());

		return aghSamisJn;
	}

	private void incluirOrigemProntuario(AghSamis samisOrigemProntuario)
			throws ApplicationBusinessException {
		this.aghSamisDAO.persistir(samisOrigemProntuario);
		this.aghSamisDAO.flush();
	}

	private void incluirSamisJn(AghSamisJn samisJn) throws ApplicationBusinessException {
		this.getAghSamisJnDAO().persistir(samisJn);
		this.getAghSamisJnDAO().flush();

	}

	private void atualizarOrigemProntuario(AghSamis samisOrigemProntuario)
			throws ApplicationBusinessException {

		try {
			this.getAghSamisDAO().atualizar(samisOrigemProntuario);
			this.getAghSamisDAO().flush();
		} catch (PersistenceException ce) {
			LOG.error("Exceção capturada: ", ce);
		}
	}

	protected AghSamisDAO getAghSamisDAO() {
		return this.aghSamisDAO;
	}

	protected AghSamisJnDAO getAghSamisJnDAO() {
		return this.aghSamisJnDAO;
	}

	public Long pesquisaCount(Integer codigoPesquisaOrigemProntuario,
			String descricaoPesquisa, DominioSituacao situacaoOrigemProntuario) {
		return getAghSamisDAO().pesquisaCount(codigoPesquisaOrigemProntuario, descricaoPesquisa, situacaoOrigemProntuario);
	}

	public List<AghSamis> pesquisaOrigemProntuario(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao) {
		return getAghSamisDAO().pesquisa(firstResult, maxResults,
				orderProperty, asc, codigo, descricao, situacao);
	}

	public AghSamis obterOrigemProntuario(Short codigoPesquisaOrigemProntuario) {
		AghSamis retorno = getAghSamisDAO().obterPorChavePrimaria(
				codigoPesquisaOrigemProntuario.shortValue());
		return retorno;
	}

	public void excluirOrigemProntuario(AghSamis samisOrigemProntuario,
			RapServidores servidorLogado, Short codigoSamisExclusao) throws ApplicationBusinessException {
		try {
			AghSamis samisExclusao = obterOrigemProntuario(codigoSamisExclusao);
			AghSamisJn samisJn = populaAghSamisJn(
					samisOrigemProntuario.getCodigo(), servidorLogado, "DEL");
			this.incluirSamisJn(samisJn);
			this.getAghSamisDAO().remover(samisExclusao);
			this.getAghSamisDAO().flush();
		} catch (Exception e) {
			LOG.error("Erro ao remover a origemProntuario.", e);
			throw new ApplicationBusinessException(
					OrigemProntuarioCRUDExceptionCode.ERRO_REMOVER_ORIGEM_PRONTUARIO);
		}

	}
	
	public List<AghSamis> pesquisaOrigemProntuarioPorCodigoOuDescricao(String objPesquisa) {
		List<AghSamis> list = null;
		if (StringUtils.isNotBlank(objPesquisa) && CoreUtil.isNumeroInteger(objPesquisa)) {
			list = getAghSamisDAO().pesquisaOrigemProntuarioPorCodigoOuDescricao(Integer.valueOf(objPesquisa), null);	
		} else {
			list = getAghSamisDAO().pesquisaOrigemProntuarioPorCodigoOuDescricao(null, objPesquisa);
		}
		return list;
	}	

}

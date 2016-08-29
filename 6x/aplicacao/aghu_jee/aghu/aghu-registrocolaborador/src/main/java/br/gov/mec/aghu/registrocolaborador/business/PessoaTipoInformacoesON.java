package br.gov.mec.aghu.registrocolaborador.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoaTipoInformacoesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PessoaTipoInformacoesON extends BaseBusiness {


@EJB
private PessoaTipoInformacoesRN pessoaTipoInformacoesRN;

private static final Log LOG = LogFactory.getLog(PessoaTipoInformacoesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapPessoaTipoInformacoesDAO rapPessoaTipoInformacoesDAO;

@Inject
private RapServidoresDAO rapServidoresDAO;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210505109402057165L;

	public enum PessoaTipoInformacoesONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PESSOA_TIPO_INFORMACAO_NAO_INFORMADA, 
		MENSAGEM_ERRO_CODIGO_PESSOA_TIPO_INFORMACAO_JA_EXISTE, 
		MENSAGEM_PARAMETRO_NAO_INFORMADO, 
		MENSAGEM_CODIGO_PESSOA_OBRIGATORIO, 
		MENSAGEM_TII_SEQ_OBRIGATORIO, 
		MENSAGEM_ERRO_REMOVER_PESSOA_TIPO_INFORMACAO, 
		MENSAGEM_ID_OBRIGATORIO, 
		MENSAGEM_VALOR_INFORMACAO_OBRIGATORIO;
	}

	
	public void salvar(RapPessoaTipoInformacoes pessoaTipoInformacoes,
			boolean alterar) throws ApplicationBusinessException {

		if (pessoaTipoInformacoes == null) {
			throw new ApplicationBusinessException(
					PessoaTipoInformacoesONExceptionCode.MENSAGEM_PESSOA_TIPO_INFORMACAO_NAO_INFORMADA);
		}

		this.validarDadosPessoaTipoInformacao(pessoaTipoInformacoes, alterar);

		if (alterar) {
			getPessoaTipoInformacoesRN().update(pessoaTipoInformacoes);
		} else {
			getPessoaTipoInformacoesRN().insert(pessoaTipoInformacoes);
		}
		getPessoaTipoInformacoesDAO().flush();
	}

	private void validarDadosPessoaTipoInformacao(
			RapPessoaTipoInformacoes pessoaTipoInformacoes, boolean alterar)
			throws ApplicationBusinessException {

		if (pessoaTipoInformacoes.getId().getPesCodigo() == null) {

			throw new ApplicationBusinessException(
					PessoaTipoInformacoesONExceptionCode.MENSAGEM_CODIGO_PESSOA_OBRIGATORIO);
		} else if (pessoaTipoInformacoes.getId().getTiiSeq() == null) {

			throw new ApplicationBusinessException(
					PessoaTipoInformacoesONExceptionCode.MENSAGEM_TII_SEQ_OBRIGATORIO);

		} else if (StringUtils.isBlank(pessoaTipoInformacoes.getValor())) {

			throw new ApplicationBusinessException(
					PessoaTipoInformacoesONExceptionCode.MENSAGEM_VALOR_INFORMACAO_OBRIGATORIO);
		}

		// No cadastro da informação complementar verifica se chave primaria ja
		// existe
		if (!alterar) {

			if (pessoaTipoInformacoes.getId() == null) {
				throw new ApplicationBusinessException(
						PessoaTipoInformacoesONExceptionCode.MENSAGEM_ID_OBRIGATORIO);
			}

			RapPessoaTipoInformacoes pessoaTipoInformacoes2 = obterPessoaTipoInformacoes(pessoaTipoInformacoes
					.getId());

			if (pessoaTipoInformacoes2 != null) {
				throw new ApplicationBusinessException(
						PessoaTipoInformacoesONExceptionCode.MENSAGEM_ERRO_CODIGO_PESSOA_TIPO_INFORMACAO_JA_EXISTE);
			}
		}
	}
		

	/**
	 * Verifica se o tipo de informação passado é um CBO
	 */
	public boolean isCbo(Short tiiSeq) throws ApplicationBusinessException {

		AghParametros aghParametrosCbo = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO);

		AghParametros aghParametrosCboSec = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO_SEC);

		AghParametros aghParametrosCbo3 = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO_3);

		AghParametros aghParametrosCbo4 = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO_4);
		AghParametros aghParametrosCbo5 = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO_5);

		if (tiiSeq == aghParametrosCbo.getVlrNumerico().shortValue()
				|| tiiSeq == aghParametrosCboSec.getVlrNumerico().shortValue()
				|| tiiSeq == aghParametrosCbo3.getVlrNumerico().shortValue()
				|| tiiSeq == aghParametrosCbo4.getVlrNumerico().shortValue()
				|| tiiSeq == aghParametrosCbo5.getVlrNumerico().shortValue()) {

			return true;

		} else {
			return false;
		}
	}

	public RapPessoaTipoInformacoes obterPessoaTipoInformacoes(
			RapPessoaTipoInformacoesId rapPessoaTipoInformacoesId)
			throws ApplicationBusinessException {
		if (rapPessoaTipoInformacoesId == null) {
			throw new ApplicationBusinessException(
					PessoaTipoInformacoesONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return getPessoaTipoInformacoesDAO().obterPorChavePrimaria(rapPessoaTipoInformacoesId);
	}

	
	public void excluirPessoaTipoInformacao(RapPessoaTipoInformacoesId id) throws ApplicationBusinessException {
		getPessoaTipoInformacoesDAO().removerPorId(id);
	}

	/**
	 * Retorna o servidor pelo id.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @return
	 */
	public RapServidores buscarServidor(Short vinculo, Integer matricula) {
		if (vinculo == null || matricula == null) {
			throw new IllegalArgumentException("Parâmetros obrigatórios.");
		}

		return getServidoresDAO().obterPorChavePrimaria(new RapServidoresId(matricula, vinculo));
	}

	protected PessoaTipoInformacoesRN getPessoaTipoInformacoesRN() {
		return pessoaTipoInformacoesRN;
	}
	
	protected RapPessoaTipoInformacoesDAO getPessoaTipoInformacoesDAO() {
		return rapPessoaTipoInformacoesDAO;
	}
	
	protected RapServidoresDAO getServidoresDAO() {
		return rapServidoresDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}

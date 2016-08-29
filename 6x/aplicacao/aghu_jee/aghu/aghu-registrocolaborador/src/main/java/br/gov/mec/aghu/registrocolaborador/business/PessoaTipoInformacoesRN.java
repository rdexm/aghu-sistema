package br.gov.mec.aghu.registrocolaborador.business;

import java.math.BigInteger;
import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoaTipoInformacoesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PessoaTipoInformacoesRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PessoaTipoInformacoesRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@Inject
	private RapPessoaTipoInformacoesDAO rapPessoaTipoInformacoesDAO;
	
	@Inject
	private RapPessoasFisicasDAO rapPessoasFisicasDAO;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5377805346926091045L;

	public enum PessoaTipoInformacoesRNExceptionCode implements
			BusinessExceptionCode {
		
		MENSAGEM_PESSOA_FISICA_NAO_CADASTRADA, 
		MENSAGEM_TIPO_INFORMACAO_NAO_CADASTRADA, 
		MENSAGEM_TIPO_PARAMETRO_INVALIDO, 
		MENSAGEM_TIPO_PARAMETRO_INVALIDO_CARTAO_UFRGS;
	}

	
	public void insert(RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {
		// this.atribuiSequencia(qualificacao);
		this.beforeRowInsert(pessoaTipoInformacoes);
		
		RapPessoaTipoInformacoesDAO rapPessoaTipoInformacoesDAO = this.getPessoaTipoInformacoesDAO();
		rapPessoaTipoInformacoesDAO.persistir(pessoaTipoInformacoes);
		rapPessoaTipoInformacoesDAO.flush();
	}

	
	public void update(RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {
		this.beforeRowUpdate(pessoaTipoInformacoes);
		getPessoaTipoInformacoesDAO().merge(pessoaTipoInformacoes);
	}

	/**
	 * ORADB: Trigger RAPT_QLF_BRI
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void beforeRowInsert(RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {
		validar(pessoaTipoInformacoes);
		verificaCns(pessoaTipoInformacoes);
		verificaCartaoUFRGS(pessoaTipoInformacoes);
		atualizarDataServidorCriado(pessoaTipoInformacoes);
	}

	/**
	 * ORADB: Trigger RAPT_QLF_BRU
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void beforeRowUpdate(RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {
		validar(pessoaTipoInformacoes);
		verificaCns(pessoaTipoInformacoes);
		verificaCartaoUFRGS(pessoaTipoInformacoes);
		atualizarDataServidorAlterado(pessoaTipoInformacoes);
	}

	/**
	 * Realiza validações.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	private void validar(RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {
		// to do

	}

	/**
	 * Atualiza as propriedades criadoPor/alteradoPor e data de atualizacao.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */

	private void atualizarDataServidorCriado(
			RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {

		// atribui pessoa fisica
		pessoaTipoInformacoes
				.setPessoaFisica(obterPessoaFisica(pessoaTipoInformacoes
						.getId().getPesCodigo()));

		// atribui tipo informacao
		pessoaTipoInformacoes
				.setTipoInformacao(obterTipoInformacoes(pessoaTipoInformacoes
						.getId().getTiiSeq().intValue()));

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		pessoaTipoInformacoes.setCriadoPor(servidorLogado);
		pessoaTipoInformacoes.setCriadoEm(Calendar.getInstance().getTime());

	}

	private void atualizarDataServidorAlterado(
			RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {

		// atribui pessoa fisica
		pessoaTipoInformacoes.setPessoaFisica(obterPessoaFisica(pessoaTipoInformacoes.getPessoaFisica().getCodigo()));

		// atribui tipo informacao
		pessoaTipoInformacoes.setTipoInformacao(obterTipoInformacoes(pessoaTipoInformacoes.getId().getTiiSeq().intValue()));

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		pessoaTipoInformacoes.setAlteradoPor(servidorLogado);

		pessoaTipoInformacoes.setAlteradoEm(Calendar.getInstance().getTime());
	}

	/**
	 * Retorna a pessoa fisica pelo id fornecido.
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected RapPessoasFisicas obterPessoaFisica(Integer id)
			throws ApplicationBusinessException {
		if (id == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		RapPessoasFisicas pessoaFisica = getPessoasFisicasDAO().obterPorChavePrimaria(id);

		if (pessoaFisica == null) {
			throw new ApplicationBusinessException(
					PessoaTipoInformacoesRNExceptionCode.MENSAGEM_PESSOA_FISICA_NAO_CADASTRADA);
		}

		return pessoaFisica;
	}

	protected RapTipoInformacoes obterTipoInformacoes(Integer id)
			throws ApplicationBusinessException {
		if (id == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		RapTipoInformacoes tipoInformacao = this.getCadastrosBasicosFacade()
				.obterTipoInformacoes(id);

		if (tipoInformacao == null) {
			throw new ApplicationBusinessException(
					PessoaTipoInformacoesRNExceptionCode.MENSAGEM_TIPO_INFORMACAO_NAO_CADASTRADA);
		}
		return tipoInformacao;
	}

	private void verificaCns(RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {

		// se tipo da informação for Cartão SUS
		AghParametros aghParametros = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CNS);

		if (pessoaTipoInformacoes.getId().getTiiSeq() == aghParametros
				.getVlrNumerico().shortValue()) {

			try {
				BigInteger bigTiiSeq = new BigInteger(
						pessoaTipoInformacoes.getValor());
				this.getCadastroPacienteFacade().validarNumeroCartaoSaude(bigTiiSeq);
			} catch (NumberFormatException e) {
				throw new ApplicationBusinessException(
						PessoaTipoInformacoesRNExceptionCode.MENSAGEM_TIPO_PARAMETRO_INVALIDO);
			}
		}
	}

	private void verificaCartaoUFRGS(
			RapPessoaTipoInformacoes pessoaTipoInformacoes)
			throws ApplicationBusinessException {

		if (pessoaTipoInformacoes.getId().getTiiSeq() == 1) {
			try {
				Integer.valueOf(pessoaTipoInformacoes.getValor());
			} catch (NumberFormatException e) {
				throw new ApplicationBusinessException(
						PessoaTipoInformacoesRNExceptionCode.MENSAGEM_TIPO_PARAMETRO_INVALIDO_CARTAO_UFRGS);
			} catch (Exception e) {
				logError(e.getMessage(),e);
			}
		}
	}

	protected RapPessoaTipoInformacoesDAO getPessoaTipoInformacoesDAO() {
		return rapPessoaTipoInformacoesDAO; 
	}
	
	protected RapPessoasFisicasDAO getPessoasFisicasDAO() {
		return rapPessoasFisicasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return (ICadastroPacienteFacade) cadastroPacienteFacade;
	}
	
	protected ICadastrosBasicosFacade getCadastrosBasicosFacade() {
		return cadastrosBasicosFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}

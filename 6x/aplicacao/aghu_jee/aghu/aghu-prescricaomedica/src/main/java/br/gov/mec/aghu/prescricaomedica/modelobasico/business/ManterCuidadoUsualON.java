package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnfId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualUnfDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * @author mgoulart
 * 
 */
@Stateless
public class ManterCuidadoUsualON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterCuidadoUsualON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmCuidadoUsualUnfDAO mpmCuidadoUsualUnfDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8064403242774335464L;

	public enum ManterCuidadoUsualONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVIDOR_INVALIDO, //
		MENSAGEM_TIPO_FREQUENCIA_NAO_CADASTRADO, // MPM-02075
		MENSAGEM_TIPO_FREQUENCIA_INATIVO, // MPM-00777
		MENSAGEM_FREQUENCIA_OBRIGATORIA, // MPM-01204
		MENSAGEM_NAO_INFORMAR_FREQUENCIA, // MPM-00930
		MPM_02746,
		MENSAGEM_UNIDADE_FUNCIONAL_CUIDADO_OBRIGATORIA;		
		// FAT-00152
	}

	public void alterar(MpmCuidadoUsual cuidadoUsual, List<Short> ufsInseridas,  List<Short> ufsExcluidas) throws ApplicationBusinessException {
		this.valida(cuidadoUsual);
		getFaturamentoFacade().atualizaFaturamentoInternoParaMpmCuidadoUsual(cuidadoUsual);

		cuidadoUsual = this.getMpmCuidadoUsualDAO().atualizar(cuidadoUsual);
		this.atualizaUnidadesFuncionais(cuidadoUsual, ufsInseridas, ufsExcluidas);
	}

	private void atualizaUnidadesFuncionais(MpmCuidadoUsual cuidadoUsual, List<Short> ufsInseridas,  List<Short> ufsExcluidas) {

		for (Short seq : ufsInseridas) {
			inserirMpmCuidadoUsualUnf(cuidadoUsual, seq);
		}

		for (Short seq : ufsExcluidas) {
			final MpmCuidadoUsualUnf mcuu = getMpmCuidadoUsualUnfDAO().obterPorChavePrimaria(cuidadoUsual.getSeq(), seq);
			if(mcuu != null){
				getMpmCuidadoUsualUnfDAO().remover(mcuu);
			}
		}
	}

	private void inserirMpmCuidadoUsualUnf(MpmCuidadoUsual cuidadoUsual, Short unfSeq) {
		
		final AghUnidadesFuncionais unf = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);

		if(getMpmCuidadoUsualUnfDAO().obterPorChavePrimaria(cuidadoUsual.getSeq(), unfSeq) == null){

			final MpmCuidadoUsualUnf mcuu = new MpmCuidadoUsualUnf();
			
			mcuu.setId(new MpmCuidadoUsualUnfId(cuidadoUsual.getSeq(), unf.getSeq()));
			mcuu.setUnidadeFuncional(unf);
			mcuu.setMpmCuidadoUsuais(cuidadoUsual);
			mcuu.setCriadoEm(new Date());
			mcuu.setRapServidores(cuidadoUsual.getRapServidores());
			
			this.getMpmCuidadoUsualUnfDAO().persistir(mcuu);
		}
	} 

	public void inserir(MpmCuidadoUsual cuidadoUsual, List<Short> ufsInseridas) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		cuidadoUsual.setRapServidores(servidorLogado);
		cuidadoUsual.setCriadoEm(Calendar.getInstance().getTime());
		cuidadoUsual.setIndCci(false);
		this.valida(cuidadoUsual);

		this.getMpmCuidadoUsualDAO().persistir(cuidadoUsual);

		cuidadoUsual = this.getMpmCuidadoUsualDAO().obterPorChavePrimaria(cuidadoUsual.getSeq());
		getFaturamentoFacade().insereFaturamentoHospitalInternoParaMpmCuidadoUsual(cuidadoUsual);

		for (Short seq : ufsInseridas) {
			inserirMpmCuidadoUsualUnf(cuidadoUsual, seq);
		}
	}

	private void valida(MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException {

		validaFrequenciaAprazamento(cuidadoUsual);
		validaServidor(cuidadoUsual.getRapServidores());
		validarIndComplemento(cuidadoUsual);

	}

	private void validaServidor(RapServidores servidor) throws ApplicationBusinessException {

		if (servidor == null || servidor.getId() == null
				|| servidor.getId().getVinCodigo() == null
				|| servidor.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(ManterCuidadoUsualONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		RapServidores result = this.getRegistroColaboradorFacade().buscaServidor(servidor.getId());
		if (result == null) {
			throw new ApplicationBusinessException(ManterCuidadoUsualONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
	}

	protected void validaFrequenciaAprazamento(MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException {

		if (cuidadoUsual.getFrequencia() != null) {
			MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento = cuidadoUsual.getMpmTipoFreqAprazamentos();

			if (mpmTipoFrequenciaAprazamento == null) {
				throw new ApplicationBusinessException(
						ManterCuidadoUsualONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_NAO_CADASTRADO);
			}

			if (!mpmTipoFrequenciaAprazamento.getIndSituacao().isAtivo()) {
				throw new ApplicationBusinessException(
						ManterCuidadoUsualONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_INATIVO);
			}

			if (!mpmTipoFrequenciaAprazamento.getIndDigitaFrequencia()) {
				throw new ApplicationBusinessException(
						ManterCuidadoUsualONExceptionCode.MENSAGEM_NAO_INFORMAR_FREQUENCIA);
			}
		} else if ((cuidadoUsual.getFrequencia() == null) && (cuidadoUsual.getMpmTipoFreqAprazamentos() != null
				&& cuidadoUsual.getMpmTipoFreqAprazamentos().getIndDigitaFrequencia()!= null 
				&& cuidadoUsual.getMpmTipoFreqAprazamentos().getIndDigitaFrequencia())) {  
			throw new ApplicationBusinessException(
					ManterCuidadoUsualONExceptionCode.MENSAGEM_FREQUENCIA_OBRIGATORIA);
		}
	}

	/**
	 * @ORADB Trigger MPMT_CDU_BRI / MPMT_CDU_BRU
	 * 
	 * @param cuidadoUsual
	 * @throws ApplicationBusinessException
	 */
	private void validarIndComplemento(MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException {

		if((cuidadoUsual.getIndOutros() != null &&  cuidadoUsual.getIndOutros())
				&& (cuidadoUsual.getIndDigitaComplemento() != null && !cuidadoUsual.getIndDigitaComplemento())) {

			throw new ApplicationBusinessException(ManterCuidadoUsualONExceptionCode.MPM_02746);
		}

	}

	protected MpmCuidadoUsualDAO getMpmCuidadoUsualDAO() {
		return mpmCuidadoUsualDAO;
	}

	

	protected MpmCuidadoUsualUnfDAO getMpmCuidadoUsualUnfDAO() {
		return mpmCuidadoUsualUnfDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	public List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual(
			Integer seqCuidado) throws ApplicationBusinessException {
		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
		.obterPorChavePrimaria(seqCuidado);

		List<MpmCuidadoUsualUnf> result = getMpmCuidadoUsualDAO()
		.listaUnidadeFuncionalPorCuidadoUsual(cuidado);

		if (result == null) {
			result = new ArrayList<MpmCuidadoUsualUnf>();
		}
		return result;
	}
	
	public List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual() {
		List<MpmCuidadoUsualUnf> result = getMpmCuidadoUsualUnfDAO().listaUnidadeFuncionalPorCuidadoUsual();
		if (result == null) {
			result = new ArrayList<MpmCuidadoUsualUnf>();
		}
		return result;
	}

	public void excluir(MpmCuidadoUsualUnf mpmCuidadoUsualunf, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		mpmCuidadoUsualunf = this.getMpmCuidadoUsualUnfDAO().obterPorChavePrimaria(mpmCuidadoUsualunf.getId());
		this.getMpmCuidadoUsualUnfDAO().deletarMpmCuidadoUsualUnf(mpmCuidadoUsualunf,servidorLogado);
		this.getMpmCuidadoUsualUnfDAO().flush();
	}

	public void incluirCuidadosUnf(AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual, RapServidores rapServidores) throws ApplicationBusinessException {
		if (unidadeFuncionalPesquisaCuidadoUsual == null) {
			throw new ApplicationBusinessException(
					ManterCuidadoUsualONExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_CUIDADO_OBRIGATORIA);
		}	
		this.getMpmCuidadoUsualUnfDAO().inserirCuidadosUnf(unidadeFuncionalPesquisaCuidadoUsual,rapServidores);
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
}
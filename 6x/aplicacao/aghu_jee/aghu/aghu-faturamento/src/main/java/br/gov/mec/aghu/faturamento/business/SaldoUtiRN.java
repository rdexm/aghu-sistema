package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.model.FatSaldoUti;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @Deprecated
 * Utilizar FatSaldoUTIRN
 * <p>
 * Triggers de:<br/>
 * ORADB: <code>FAT_SALDOS_UTI</code>
 * </p>
 * 
 * @author gandriotti
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class SaldoUtiRN	extends AbstractAGHUCrudRn<FatSaldoUti> {

	@EJB
	private SaldoUtiAtualizacaoRN saldoUtiAtualizacaoRN;
	
	private static final Log LOG = LogFactory.getLog(SaldoUtiRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5375578253747303428L;

	protected void ajustarDadosAlteracaoEntidade(final FatSaldoUti entidade, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		entidade.setAlteradoEm(this.getDataCriacao());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00004);
		}
		entidade.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
	}

	/*
	 * Necessario para testes unitarios
	 */
	protected int getCapacidadeMaximaUti(final FatSaldoUti entidade) {
		int cap = 0;
		Integer mes = entidade.getId().getMes(); 
		Integer ano = entidade.getId().getAno(); 
		Integer qtdLeitos = entidade.getNroLeitos();
		cap = saldoUtiAtualizacaoRN.obterCapMaxUti(mes, ano, qtdLeitos);

		return cap;
	}

	/**
	 * ORADB: <code>FATT_SUT_BRI</code>
	 * 
	 * @see FaturamentoExceptionCode#FAT_00686
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean briPreInsercaoRow(final FatSaldoUti entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;
		int cap = 0;

		this.ajustarDadosAlteracaoEntidade(entidade, dataFimVinculoServidor);
		cap = this.getCapacidadeMaximaUti(entidade);
		entidade.setCapacidade(Integer.valueOf(cap));
		entidade.setCriadoEm(entidade.getAlteradoEm());
		entidade.setCriadoPor(entidade.getAlteradoPor());
		result = true;

		return result;
	}

	/**
	 * ORADB: <code>FATT_SUT_BRU</code>
	 * 
	 * @see FaturamentoExceptionCode#FAT_00686
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean bruPreAtualizacaoRow(final FatSaldoUti original, final FatSaldoUti modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;
		int cap = 0;

		if (modificada.getNroLeitos().intValue() != original.getNroLeitos().intValue()) {
			cap = this.getCapacidadeMaximaUti(modificada);
			modificada.setCapacidade(Integer.valueOf(cap));
		}
		this.ajustarDadosAlteracaoEntidade(modificada, dataFimVinculoServidor);
		result = true;

		return result;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}

package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.model.FatBancoCapacidade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @Deprecated
 * Utilizar FatBancoCapacidadeRN
 * Triggers de:<br/>
 * ORADB: <code>FAT_BANCO_CAPACIDADES</code>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
@Deprecated
public class BancoCapacidadeRN extends AbstractAGHUCrudRn<FatBancoCapacidade> {
	
	
	private static final Log LOG = LogFactory.getLog(BancoCapacidadeRN.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3263057435826706261L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	protected void ajustarDadosAlteracaoEntidade(FatBancoCapacidade entidade)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date data = null;
		
		data = this.getDataCriacao();
		entidade.setAlteradoEm(data);
		entidade.setServidorAltera(servidorLogado);
	}
	
	/*
	 * Necessario para testes unitarios
	 */
	protected int getCapacidadeMaximaUti(FatBancoCapacidade entidade) {
		
		int cap = 0;
		Date comp = null;
		int ano = 0;
		int mes = 0;
		int qtdLeitos = 0;
		
		ano = entidade.getId().getAno().intValue();
		mes = entidade.getId().getMes().intValue();
		comp = ManipulacaoDatasUtil.getDate(ano, mes, 1);
		qtdLeitos = entidade.getNroLeitos().intValue();
		cap = ManipulacaoDatasUtil.getMonthDayAmount(comp) * qtdLeitos;
		
		return cap;
	}

	/**
	 * <p>
	 * ORADB: <code>FATT_FBC_BRI</code>
	 * </p>
	 */
	@Override
	public boolean briPreInsercaoRow(FatBancoCapacidade entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		
		boolean result = false;
		int cap = 0;
				
		this.ajustarDadosAlteracaoEntidade(entidade);
		//TODO calcular a capacidade?
		cap = this.getCapacidadeMaximaUti(entidade);
		entidade.setCapacidade(Integer.valueOf(cap));
		entidade.setCriadoEm(entidade.getAlteradoEm());
		entidade.setServidor(entidade.getServidor());
		result = true;

		return result;
	}
	
	/**
	 * <p>
	 * ORADB: <code>FATT_FBC_BRU</code>
	 * </p>
	 */
	@Override
	public boolean bruPreAtualizacaoRow(FatBancoCapacidade original,
			FatBancoCapacidade modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		boolean result = false;
		int cap = 0;
		
		// calcula a nova capacidade em funcao dos dias do mes e nro de leitos
		if (modificada.getNroLeitos().intValue() != original.getNroLeitos().intValue()) {
			cap = this.getCapacidadeMaximaUti(modificada);
			modificada.setCapacidade(Integer.valueOf(cap));
		} else {
			cap = modificada.getCapacidade().intValue();
		}
		// verifica se nova capacidade é superior ao saldo atual
		/*
		// verificacao foi comentada no AGH
		if (cap < modificada.getUtilizado().intValue()) {
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.FAT_00000, 
					modificada.getCapacidade(),
					modificada.getUtilizado());
		}
		*/
		
		this.ajustarDadosAlteracaoEntidade(modificada);
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

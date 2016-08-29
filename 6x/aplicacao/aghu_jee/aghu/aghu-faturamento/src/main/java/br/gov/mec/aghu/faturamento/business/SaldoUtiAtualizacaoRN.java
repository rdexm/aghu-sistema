package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAtualizacaoSaldo;
import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.faturamento.dao.FatSaltoUtiDAO;
import br.gov.mec.aghu.faturamento.vo.RnSutcAtuSaldoVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatSaldoUti;
import br.gov.mec.aghu.model.FatSaldoUtiId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * <p>
 * Linhas: 351 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 1 <br/>
 * Consultas: 3 tabelas <br/>
 * Alteracoes: 8 tabelas <br/>
 * Metodos: 4 <br/>
 * Metodos externos: 2 <br/>
 * Tempo: 21.0 <br/>
 * Pontos: 2.6 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_SUT_RN</code>
 * </p>
 * 
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class SaldoUtiAtualizacaoRN
		extends AbstractFatDebugLogEnableRN {

private static final String MES = " MES ";

private static final Log LOG = LogFactory.getLog(SaldoUtiAtualizacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatSaltoUtiDAO fatSaltoUtiDAO;

	static class PartialVO {

		public boolean retorno = false;
		public String msg = "";
	}

	public enum TipoCompetenciaEnum {

		INICIAL, ANTERIOR, ALTA, ;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6218143003437171773L;
	
	@EJB
	private SaldoUtiPersist saldoUtiPersist;

	protected SaldoUtiPersist getSaldoUtiON() {
		return saldoUtiPersist;
	}

	protected FatSaltoUtiDAO getFatSaltoUtiDAO() {

		return fatSaltoUtiDAO;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUT_RN.RN_SUTC_ATU_CAPACID</code>
	 * </p>
	 * 
	 * @param saldoUti
	 *            serao considerados apenas os dados de mes e ano
	 * @return
	 */
	protected Integer obterCapMaxUti(Integer mes, Integer ano, Integer qtdLeitos) {
		Date comp = ManipulacaoDatasUtil.getDate(ano, mes, 1);
		Integer qtdDias = ManipulacaoDatasUtil.getMonthDayAmount(comp);
		return qtdDias * qtdLeitos;
	}

	protected FatSaldoUti obterFatSaldoUti(final int ano, final int mes, final DominioTipoIdadeUTI tipo) {
		return getFatSaltoUtiDAO().obterPorChavePrimaria(new FatSaldoUtiId(Integer.valueOf(mes), Integer.valueOf(ano), tipo));
	}

	protected FatSaldoUti obterNovoFatSaldoUti(final int ano,
			final int mes,
			final int dias,
			final int cap,
			final DominioTipoIdadeUTI tipo,
			final Integer nrLeitos) {


		FatSaldoUti saldoUtiCorrente = new FatSaldoUti();
		FatSaldoUtiId saldoUtiId = new FatSaldoUtiId(Integer.valueOf(mes), Integer.valueOf(ano), tipo);
		saldoUtiCorrente.setId(saldoUtiId);
		saldoUtiCorrente.setNroLeitos(nrLeitos);
		saldoUtiCorrente.setCapacidade(Integer.valueOf(cap));
		saldoUtiCorrente.setSaldo(Integer.valueOf(dias));

		return saldoUtiCorrente;
	}

	/**
	 * ORADB: FATK_SUT_RN.RN_SUTC_ATU_INCSALDO 
	 */
	protected PartialVO incrementarSaldoDiariasUti(final DominioTipoIdadeUTI idadeUti, final int ano, final int mes, 
												   final int pDias, TipoCompetenciaEnum tipoCompetencia, String nomeMicrocomputador) 
												  throws IllegalStateException, BaseException {

		SaldoUtiPersist saldoOn = this.getSaldoUtiON();
		PartialVO result = new PartialVO();
		int vCapacidade = 0;
		int saldo = 0;

		FatSaldoUti regSaldo = this.obterFatSaldoUti(ano, mes, idadeUti);
		
		// ja existe registro para o mes/ano desejado
		if (regSaldo != null) { // atualiza saldo
			logar("JA existe registro para o mes/ano: {0}/{1} tipo: {2}", mes, ano, idadeUti);
			logar("atualiza saldo. CAPAC={0}", regSaldo.getCapacidade());
			
			vCapacidade = regSaldo.getCapacidade().intValue();
			saldo = regSaldo.getSaldo().intValue() + pDias;
			
			regSaldo.setSaldo(Integer.valueOf(saldo));
			saldoOn.atualizar(regSaldo, nomeMicrocomputador, new Date());
			
			if (vCapacidade < saldo) { // estourou capacidade
				logar("estourou capacidade. CAPAC={0}", regSaldo.getCapacidade());
				result.msg += "ESTOUROU BANCO DE UTI "+idadeUti.getDescricao().toUpperCase()+MES+tipoCompetencia+' ';
				result.retorno = false;
			}
			
		} else { // nao existe registro para o mes/ano desejado
			logar("NAO existe registro para o mes/ano: {0}/{1} tipo: {2}", mes,ano,idadeUti);

			final List<Integer> nrLeitosList = getFatSaltoUtiDAO().obterListNroLeitosPorTipoEmOrdemDecrData(idadeUti);
			
			if (nrLeitosList != null && !nrLeitosList.isEmpty()) {
				String msg=null;
				// existe registro de outro mes/ano para o tipo				
				Integer nroLeitos = nrLeitosList.get(0);
				if (nroLeitos == null) {
					nroLeitos = 0;
				}
				
				logar("existe registro de outro mes/ano do tipo: {0}", idadeUti);
				
				// calcula qtd maxima de uti cfe nro dias do mes e nro leitos da uti
				vCapacidade = this.obterCapMaxUti(mes, ano, nroLeitos);
				regSaldo = this.obterNovoFatSaldoUti(ano, mes, pDias, vCapacidade, idadeUti, nroLeitos);
				
				logar("insere registro de saldo para o mes/ano:{0}/{1} tipo: {2}", mes,ano,idadeUti);
				try {
					saldoOn.inserir(regSaldo, nomeMicrocomputador, new Date());
				} catch (Exception e) {
					logar("erro ao inserir saldo para o mes/ano:{0}/{1} tipo: {2}", mes,ano,idadeUti);
					msg = e.getMessage();
					result.msg = result.msg + " ERRO AO INSERIR SALDO UTI " + msg;
					result.retorno = false;
				}
				result.retorno = true;
				if (vCapacidade < pDias) { // estourou capacidade
					logar("estourou capacidade. CAPAC={0}", vCapacidade);
					result.msg += "ESTOUROU BANCO DE UTI "+idadeUti.getDescricao().toUpperCase()+MES+tipoCompetencia;
					result.retorno = false;
				}
				
			} else { // nao existe registro de outro mes/ano para o tipo
				logar("NAO existe registro de outro mes/ano do tipo:{0}", idadeUti);

				result.msg += "NAO FOI POSSIVEL IDENTIFICAR CAPACIDADE DO BANCO DE UTI PARA O TIPO "+idadeUti.getDescricao().toUpperCase()+
				  			  MES+tipoCompetencia+' '+mes+'/'+ano;
				
				result.retorno = false;
			}
		}
		
		logar("v_mensagem: {0}",result.msg);
		
		// -- Marina 02/10/2009-- Solicitação da Ruth para não criticar mais a capacidade do Banco UTI
		result.retorno = true;

		return result;
	}
	
	/**
	 * ORADB: <code>FATK_SUT_RN.RN_SUTC_ATU_DECSALDO
	 */
	protected PartialVO decrementarSaldoDiariasUti(final DominioTipoIdadeUTI idadeUti,
			final int ano,
			final int mes,
			final int pDias,
			TipoCompetenciaEnum pCampoMes, String nomeMicrocomputador) throws IllegalStateException, BaseException {

		SaldoUtiPersist saldoOn = this.getSaldoUtiON();
		PartialVO result = new PartialVO();
		int saldo = 0;

		final FatSaldoUti regSaldo = this.obterFatSaldoUti(ano, mes, idadeUti);
		
		// ja existe registro para o mes/ano desejado
		if (regSaldo != null) {

			logar("existe registro para o mes/ano: {0}/{1} tipo: {2}", mes, ano, idadeUti);
			saldo = regSaldo.getSaldo().intValue();
			
			if (saldo >= pDias) {
				// atualiza saldo
				logar("atualiza saldo");
				saldo = regSaldo.getSaldo().intValue() - pDias;
				regSaldo.setSaldo(Integer.valueOf(saldo));				
				saldoOn.atualizar(regSaldo, nomeMicrocomputador, new Date());
				
			} else if(saldo < pDias){
				logar("saldo menor que p_dias");
				result.msg += "FALTOU SALDO NO BANCO DE UTI PARA DESCONTAR "+(pDias-saldo)+
							  " DIARIAS TIPO "+idadeUti.getDescricao().toUpperCase()+MES+pCampoMes+' '+mes+'/'+ano+' ';
				result.retorno = false;
			}
			
		} else { 
			// nao existe registro para o mes/ano desejado
			logar("NAO existe registro para o mes/ano: {0}/{1} tipo: {2}",mes,ano,idadeUti);
			result.msg += "NAO FOI POSSIVEL IDENTIFICAR CAPACIDADE DO BANCO DE UTI PARA O TIPO "+idadeUti.getDescricao().toUpperCase()+
						  MES+pCampoMes+' '+mes+'/'+ano+' ';
			result.retorno = false;
		}
		
		// -- Marina 02/10/2009-- Solicitação da Ruth para não criticar mais a capacidade do Banco UTI
		result.retorno = true;

		return result;
	}

	/**
	 * <p>
	 * <b>NOTA IMPORTANTE:</b><br/>
	 * Esta rotina eh "chumbada" para apenas considerar ateh 3 meses de
	 * internacao de UTI. Isso se deve ao "chumbamento" da tabela
	 * {@link FatContasHospitalares} que possui (amarrada) no codigo apenas
	 * entradas para ateh 3 meses de internacao: <br/>
	 * {@link FatContasHospitalares#getDiasUtiMesInicial()} (atual - 2 meses)<br/>
	 * {@link FatContasHospitalares#getDiasUtiMesAnterior()} (atual - 1 mes)<br/>
	 * {@link FatContasHospitalares#getDiasUtiMesAlta()} (atual)<br/>
	 * </p>
	 * <p>
	 * Atualizar saldo diarias do Banco de UTI <br/>
	 * TODO savepoints e rollbacks<br/>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_SUT_RN.RN_SUTC_ATU_SALDO</code>
	 * </p>
	 * 
	 * @param operacao
	 * @param idadeUti
	 * @param dataAltaAdm
	 * @param quantidadeDiariasIni
	 * @param quantidadeDiariasAnt
	 * @param quantidadeDiariasAlta
	 * @return
	 * @throws BaseException
	 * @throws IllegalStateException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public RnSutcAtuSaldoVO atualizarSaldoDiariasUti(final DominioAtualizacaoSaldo operacao,			
			final Date pDtAltaAdm,
			final int pDiasIni,
			final int pDiasAnt,
			final int pDiasAlta,
			final DominioTipoIdadeUTI pIdadeUTI, String nomeMicrocomputador) throws IllegalStateException, BaseException {

		boolean retorno = false;
		PartialVO saldoIni = null;
		PartialVO saldoAnt = null;
		PartialVO saldoAlta = null;
		String msgInicial = null;
		String msgAnterior = null;
		String msgAlta = null;
		
		int vMesIni = 0;
		int vAnoIni = 0;
		
		// savepoint fat_svp_rn_sutc_atu_saldo_inc;
		logar("savepoint {0}", (DominioAtualizacaoSaldo.I.equals(operacao)) ? "incrementa" : "decrementa" );

		// atualiza saldo mes inicial
		if (pDiasIni > 0) {
			
			// mes/ano dos campos de uti do cma
			vMesIni = Integer.parseInt(DateUtil.dataToString(DateUtil.adicionaMeses(pDtAltaAdm, -2), "MM"));
			vAnoIni = Integer.parseInt(DateUtil.dataToString(DateUtil.adicionaMeses(pDtAltaAdm, -2), "yyyy"));
			
			if(DominioAtualizacaoSaldo.I.equals(operacao)){
				saldoIni = this.incrementarSaldoDiariasUti(pIdadeUTI, vAnoIni, vMesIni, pDiasIni, TipoCompetenciaEnum.INICIAL, nomeMicrocomputador);
				msgInicial = saldoIni.msg;
				logar("P_mensagem_INI: {0}", saldoIni.msg );
				
			} else {
				saldoIni = this.decrementarSaldoDiariasUti(pIdadeUTI, vAnoIni, vMesIni, pDiasIni, TipoCompetenciaEnum.INICIAL, nomeMicrocomputador);
			}
		}
		
		// atualiza saldo mes anterior
		if (pDiasAnt > 0) {
			
			// mes/ano dos campos de uti do cma
			vMesIni = Integer.parseInt(DateUtil.dataToString(DateUtil.adicionaMeses(pDtAltaAdm, -1), "MM"));
			vAnoIni = Integer.parseInt(DateUtil.dataToString(DateUtil.adicionaMeses(pDtAltaAdm, -1), "yyyy"));
			
			if(DominioAtualizacaoSaldo.I.equals(operacao)){
				saldoAnt = this.incrementarSaldoDiariasUti(pIdadeUTI, vAnoIni, vMesIni, pDiasIni, TipoCompetenciaEnum.ANTERIOR, nomeMicrocomputador);
			} else {
				saldoAnt = this.decrementarSaldoDiariasUti(pIdadeUTI, vAnoIni, vMesIni, pDiasIni, TipoCompetenciaEnum.ANTERIOR, nomeMicrocomputador);
			}
			
			msgAlta = saldoAnt.msg;
			logar("P_mensagem_ANT: {0}", saldoAnt.msg );
		}
		
		// atualiza saldo mes alta
		if (pDiasAlta > 0) {
			// mes/ano dos campos de uti do cma
			vMesIni = Integer.parseInt(DateUtil.dataToString(pDtAltaAdm, "MM"));
			vAnoIni = Integer.parseInt(DateUtil.dataToString(pDtAltaAdm, "yyyy"));
			
			if(DominioAtualizacaoSaldo.I.equals(operacao)){
				saldoAlta = this.incrementarSaldoDiariasUti(pIdadeUTI, vAnoIni, vMesIni, pDiasIni, TipoCompetenciaEnum.ALTA, nomeMicrocomputador);
			} else {
				saldoAlta = this.decrementarSaldoDiariasUti(pIdadeUTI, vAnoIni, vMesIni, pDiasIni, TipoCompetenciaEnum.ALTA, nomeMicrocomputador);
			}
			msgAlta = saldoAlta.msg;
			logar("P_mensagem_INI: {0}",saldoAlta.msg);
		}
		
		// se deu errado a atualizacao do banco de uti para algum dos meses,
		// deve desfazer a atualizacao de todos meses
		if (
			 ( (saldoIni != null)  && !saldoIni.retorno  ) || 
			 ( (saldoAnt != null)  && !saldoAnt.retorno  ) || 
			 ( (saldoAlta != null) && !saldoAlta.retorno )
		   ) {
			
			// TODO rollback to fat_svp_rn_sutc_atu_saldo_inc;
		    logar("rollback {0}", (DominioAtualizacaoSaldo.I.equals(operacao)) ? "incrementa" : "decrementa");
			retorno = false;
		}
		
		// -- Marina 02/10/2009-- Solicitação da Ruth para não criticar mais a capacidade do Banco UTI
		retorno = true;
		
		
		//VO
		return new RnSutcAtuSaldoVO(msgInicial, msgAnterior, msgAlta, retorno);
	}
}

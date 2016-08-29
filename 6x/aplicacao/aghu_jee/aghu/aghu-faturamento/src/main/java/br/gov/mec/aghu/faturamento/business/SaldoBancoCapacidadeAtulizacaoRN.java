package br.gov.mec.aghu.faturamento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAtualizacaoSaldo;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.dao.FatBancoCapacidadeDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.FatBancoCapacidade;
import br.gov.mec.aghu.model.FatBancoCapacidadeId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ORADB: <code>FATK_FBC_RN</code>
 * 
 * @author eschweigert
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class SaldoBancoCapacidadeAtulizacaoRN
		extends AbstractFatDebugLogEnableRN {


@EJB
private FatkCth5RN fatkCth5RN;

private static final Log LOG = LogFactory.getLog(SaldoBancoCapacidadeAtulizacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatContasHospitalaresDAO fatContasHospitalaresDAO;

@EJB
private IInternacaoFacade internacaoFacade;

@Inject
private FatEspelhoAihDAO fatEspelhoAihDAO;

@Inject
private FatBancoCapacidadeDAO fatBancoCapacidadeDAO;

@EJB
private BancoCapacidadePersist bancoCapacidadePersist;

@EJB
private EspelhoAihPersist espelhoAihPersist;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8208551326080365161L;

	protected FatBancoCapacidadeDAO getFatBancoCapacidadeDao() {

		return fatBancoCapacidadeDAO;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDao() {
		return fatContasHospitalaresDAO;
	}

	protected FatEspelhoAihDAO getFatEspelhoAihDao() {
		return fatEspelhoAihDAO;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this
				.internacaoFacade;
	}

	protected BancoCapacidadePersist getBancoCapacidadePersist() {
		return bancoCapacidadePersist;
	}

	protected EspelhoAihPersist getEspelhoAihPersist() {
		return espelhoAihPersist;
	}

	protected int obterQuantidadeLeitosAtivosPorCodigoClinica(final Integer codigoClinica) {

		int result = 0;
		final List<Integer> quartos = getInternacaoFacade().obterListNumeroQuartoPorCodigoClinica(codigoClinica);
		
		if(quartos != null && !quartos.isEmpty()) {
			result = getInternacaoFacade().contarLeitosAtivosPorListaQuartos(quartos);
		}

		return result;
	}

	/**
	 * ORADB: <code>FATK_FBC_RN.FATC_FBC_INC_SALDO</code>
	 * @param dataFimVinculoServidor 
	 */
	protected boolean incrementarSaldoDiariasBancoCapacidade(final Integer codigoClinica,
			final Integer ano, final Integer mes, final Integer dias, 
			final boolean isOrigemEncerramentoCompetencia, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {

		boolean result = true;
		FatBancoCapacidade bncCap = getFatBancoCapacidadeDao().obterPorChavePrimaria(new FatBancoCapacidadeId(ano, mes, codigoClinica));
		
		int vUtilizado = 0;
		int vCapacidade = 0;
		
		if(bncCap != null){
			vUtilizado = (bncCap.getUtilizado() != null ? bncCap.getUtilizado().intValue() : 0) + dias.intValue();
			vCapacidade = bncCap.getCapacidade().intValue();
			
			// se a origem é diferente de encerramento da comp - não testa a capacidade
			if (isOrigemEncerramentoCompetencia) {
				bncCap.setUtilizado(Integer.valueOf(vUtilizado));
				getBancoCapacidadePersist().atualizar(bncCap, nomeMicrocomputador, dataFimVinculoServidor);
				
			} else {
				if (vUtilizado <= vCapacidade) {
					bncCap.setUtilizado(Integer.valueOf(vUtilizado));
					
				// estourou capacidade
				} else { 
					this.logDebug( FaturamentoDebugCode.FAT_FBC_UTIL_MAIOR_CAP, bncCap.getUtilizado(), bncCap.getCapacidade());
					result = false;
				}
			}			
			
		} else {
			// Não existe registro para o ano, mês, clinica - insere registro       
			// Busca o nro de leitos do mês anterior
			int vMes = mes.intValue();
//			int vAno = ano.intValue();

			if(vMes == 1){
				vMes = 12;
//				vAno--;
			}
			
			int vLeitos = this.obterQuantidadeLeitosAtivosPorCodigoClinica(codigoClinica);
			
			// Busca quantidade de dias naquele mês
			// v_dias := TO_NUMBER( TO_CHAR( LAST_DAY( TO_DATE('01'||LTRIM(TO_CHAR(p_mes,'00')) ||LTRIM(TO_CHAR(p_ano,'0000')) ,'DDMMYYYY') ) ,'DD' ) );
			final Date d = DateUtil.obterUltimoDiaDoMes(DateUtil.obterData(ano, (mes - 1), 01)); 
			final Calendar cal = Calendar.getInstance(); cal.setTime(d);
			int vDias = cal.get(Calendar.DAY_OF_MONTH);
				
			vCapacidade = vLeitos * vDias;
			
			if(isOrigemEncerramentoCompetencia){
				bncCap = new FatBancoCapacidade();
				bncCap.setId(new FatBancoCapacidadeId(ano, mes, codigoClinica));
				bncCap.setNroLeitos(vLeitos);
				bncCap.setCapacidade(vCapacidade);
				bncCap.setUtilizado(vDias);
				
				getBancoCapacidadePersist().inserir(bncCap, nomeMicrocomputador, dataFimVinculoServidor);
				
			} else {
				if(dias <= vCapacidade){
					bncCap = new FatBancoCapacidade();
					bncCap.setId(new FatBancoCapacidadeId(ano, mes, codigoClinica));
					bncCap.setNroLeitos(vLeitos);
					bncCap.setCapacidade(vCapacidade);
					bncCap.setUtilizado(dias);
					
					getBancoCapacidadePersist().inserir(bncCap, nomeMicrocomputador, dataFimVinculoServidor);
					
				} else {
					result = false;
				}
			}
		}
		
		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_FBC_RN.FATC_FBC_DEC_SALDO</code>
	 * </p>
	 * 
	 * @param codigoClinica
	 * @param competencia
	 * @param quantidadeDiarias
	 * @param dataFimVinculoServidor 
	 * @return
	 * @throws BaseException
	 * @throws IllegalStateException
	 * @see FatBancoCapacidade
	 */
	protected boolean decrementarSaldoDiariasBancoCapacidade(final Integer codigoClinica,
			final Integer mes, final Integer ano,
			final int quantidadeDiarias, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {

		boolean result = false;
		int utilizado = 0;

		final FatBancoCapacidade bncCap = getFatBancoCapacidadeDao().obterPorChavePrimaria(new FatBancoCapacidadeId(ano, mes, codigoClinica)); 
		if (bncCap != null) {
			utilizado = bncCap.getUtilizado().intValue();
			if (utilizado >= quantidadeDiarias) { // atualiza saldo
				utilizado -= quantidadeDiarias;
				bncCap.setUtilizado(Integer.valueOf(utilizado));
				getBancoCapacidadePersist().setComFlush(false);
				getBancoCapacidadePersist().atualizar(bncCap, nomeMicrocomputador, dataFimVinculoServidor);
				result = true;
			} else {
				result = false;
			}
			
		// se não achou, não pode diminuir
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_FBC_RN.FATP_FBC_ATU_SALDO</code>
	 * </p>
	 * @param dataInternacao
	 * @param dataAlta
	 * @param codigoClinica
	 * @param cthSeq
	 * @param operacao 
	 * @param isOrigemEncerramentoComp se <code>'E'</code> entao <code>true</code>, caso contrario sempre <code>false</code>
	 * @param dataFimVinculoServidor 
	 * 
	 * @return
	 * @throws BaseException
	 * @throws IllegalStateException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public boolean atualizarSaldoDiariasBancoCapacidade(final Date dataInternacao,
														final Date dataAlta,
														final Integer codigoClinica,
														final Integer cthSeq,
														final DominioAtualizacaoSaldo operacao,
														final boolean isOrigemEncerramentoComp, 
														final String nomeMicrocomputador, final Date dataFimVinculoServidor) 
															throws IllegalStateException, BaseException {

		boolean result = false;

		if (operacao == null) {
			throw new IllegalArgumentException("operacao não pode ser nulo.");
		}
		if (dataInternacao == null) {
			throw new IllegalArgumentException("dataInternacao não pode ser nulo.");
		}
		if (dataAlta == null) {
			throw new IllegalArgumentException("dataAlta não pode ser nulo.");
		}
		if (dataInternacao.after(dataAlta)) {
			throw new IllegalArgumentException("Data de internacao nao pode ser anterior a data de alta."
					+ "\nInternacao: " + DateFormatUtils.ISO_DATE_FORMAT.format(dataInternacao) + "\nAlta: "
					+ DateFormatUtils.ISO_DATE_FORMAT.format(dataInternacao));
		}
		
		
		// Calcula Diárias
		
		final Calendar calInternacao = Calendar.getInstance();
		calInternacao.setTime(dataInternacao);

		// v_ano := TO_NUMBER(TO_CHAR(ADD_MONTHS(p_dt_int,0),'YYYY'));
		Integer vAno = calInternacao.get(Calendar.YEAR);
		
		// v_mes := TO_NUMBER(TO_CHAR(ADD_MONTHS(p_dt_int,0),'MM'));
		Integer vMes = calInternacao.get(Calendar.MONTH) + 1; // em java mês ZERO == Janeiro
		
		// v_ano_mes_int   := TO_CHAR(ADD_MONTHS(p_dt_int,0),'YYYY') || TO_CHAR(ADD_MONTHS(p_dt_int,0),'MM') ;
		String vAnoMesInt = vAno.toString() + vMes.toString();
		

		final Calendar calAlta = Calendar.getInstance();
		calAlta.setTime(dataAlta);
		
		// v_ano_alta := TO_NUMBER(TO_CHAR(ADD_MONTHS(p_dt_alta,0),'YYYY'));
		final Integer vAnoAlta = calAlta.get(Calendar.YEAR);
		
		final Integer vMesAlta = calAlta.get(Calendar.MONTH) + 1; // em java mês ZERO == Janeiro;
		
		final String vAnoMesAlta = vAnoAlta.toString() + vMesAlta.toString();
		
		int vDiasMes = 0;
		
		// Verifica a qtde de dias no mês da internação
		if(DateUtil.isDatasIguais(DateUtil.truncaData(dataInternacao), DateUtil.truncaData(dataAlta))){
			vDiasMes++;
			
		} else if(CoreUtil.igual(vAnoMesInt, vAnoMesAlta)){
			vDiasMes =  calAlta.get(Calendar.DAY_OF_MONTH) 	-		// (TO_NUMBER(TO_CHAR(ADD_MONTHS(p_dt_alta,0),'DD')) - 
						calInternacao.get(Calendar.DAY_OF_MONTH);	// TO_NUMBER(TO_CHAR(ADD_MONTHS(p_dt_int,0),'DD')));
			
		} else {
			final Calendar calUltDiaInternacao = Calendar.getInstance();
			calUltDiaInternacao.setTime(DateUtil.obterUltimoDiaDoMes(dataInternacao));
			
			// v_dias_mes := to_number(trunc( last_day(add_months(p_dt_int ,0)) - (to_date(P_DT_INT))));
			vDiasMes = calUltDiaInternacao.get(Calendar.DAY_OF_MONTH) - calInternacao.get(Calendar.DAY_OF_MONTH);
		}
		
		// savepoint fat_svp_rn_ctrl_diarias;
		
		int opc = 0;
		do {
			if (DominioAtualizacaoSaldo.I.equals(operacao)){
				result = incrementarSaldoDiariasBancoCapacidade(codigoClinica, vAno, vMes, vDiasMes, isOrigemEncerramentoComp, nomeMicrocomputador, dataFimVinculoServidor);
				
			} else if(DominioAtualizacaoSaldo.D.equals(operacao)){
				result = decrementarSaldoDiariasBancoCapacidade(codigoClinica, vMes, vAno, vDiasMes, nomeMicrocomputador, dataFimVinculoServidor);
			}
			
			if(result){
				
				if (CoreUtil.igual(vAnoMesInt, vAnoMesAlta)){
					// Mairna 16/09/2008
					// Verifica se teve diárias de UTI
			
					if (getFatkCth5RN().fatcValidaProcUti(cthSeq)){
						// calcula diarias UTI
						
						// Busca pelo seq da conta e faz demais controles em if abaixo...
						final FatContasHospitalares cth = getFatContasHospitalaresDao().obterPorChavePrimaria(cthSeq);

						/* AND ind_conta_reapresentada IS NULL AND
					         (DIAS_UTI_MES_ALTA > 0 OR DIAS_UTI_MES_ANTERIOR > 0 OR DIAS_UTI_MES_INICIAL > 0); */
						if( (cth.getIndContaReapresentada() == null ) && 
								(cth.getDiasUtiMesAlta() > 0 || cth.getDiasUtiMesAnterior() > 0 || cth.getDiasUtiMesInicial() > 0)
						){
							if(cth.getDiasUtiMesInicial() > 0){
								
								atualizaFatBancoCapacidade( operacao, 
															(cth.getDiasUtiMesInicial() != null ? cth.getDiasUtiMesInicial() : 0 ), 
															calInternacao.get(Calendar.YEAR), 
															(calInternacao.get(Calendar.MONTH) + 1), 
															codigoClinica, nomeMicrocomputador, dataFimVinculoServidor);
							}
							
							if(cth.getDiasUtiMesAlta() > 0){
								atualizaFatBancoCapacidade( operacao, 
															(cth.getDiasUtiMesAlta() != null ? cth.getDiasUtiMesAlta() : 0 ), 
															calAlta.get(Calendar.YEAR), 
															(calAlta.get(Calendar.MONTH) + 1), 
															codigoClinica, nomeMicrocomputador, dataFimVinculoServidor);
							}
							

							if(cth.getDiasUtiMesAnterior() > 0){
								// v_mes_ant   := TO_NUMBER(TO_CHAR(ADD_MONTHS(p_dt_alta,0),'MM')-1);
								int vMesAnt = (calAlta.get(Calendar.MONTH) + 1); // em java mês ZERO == Janeiro;
								vMesAnt--;
								
								int vAnoAnt = calAlta.get(Calendar.YEAR);
								if(vMesAnt == 1){
									vAnoAnt--;
								}
								
								atualizaFatBancoCapacidade( operacao, 
															(cth.getDiasUtiMesAnterior() != null ? cth.getDiasUtiMesAnterior() : 0 ), 
															vAnoAnt, 
															vMesAnt, 
															codigoClinica, nomeMicrocomputador, dataFimVinculoServidor);
							}
						}
					}
					
					opc = -1; // EXIT
					
				} else {
					Date date = DateUtil.obterData( Integer.parseInt(vAnoMesInt.substring(0,4)), 
							  						Integer.parseInt(vAnoMesInt.substring(4)) - 1, 
							  						1
							 					  );

					final Calendar cal = Calendar.getInstance();
					cal.setTime(DateUtil.adicionaMeses(date, 1));
					vAno = cal.get(Calendar.YEAR);
					vMes = (cal.get(Calendar.MONTH) + 1); // em java mês ZERO == Janeiro;
					vAnoMesInt = vAno.toString() + vMes.toString();
					
					// v_dias := TO_NUMBER( TO_CHAR( LAST_DAY( TO_DATE('01' ||LTRIM(TO_CHAR(v_mes,'00')) ||LTRIM(TO_CHAR(v_ano,'0000')) ,'DDMMYYYY') ),'DD' ) );
					final Date d = DateUtil.obterUltimoDiaDoMes(DateUtil.obterData(vAno, (vMes - 1), 01)); 
					final Calendar cal2 = Calendar.getInstance(); 
					cal2.setTime(d);
					int vDias = cal2.get(Calendar.DAY_OF_MONTH);
					
					if(!CoreUtil.igual(vAnoMesInt, vAnoMesAlta)){
						vDiasMes = vDias;
						
					} else {
						vDiasMes = calAlta.get(Calendar.DAY_OF_MONTH);
					}
				}
				
			// estourou capacidade
			} else {
				// TODO rollback to fat_svp_rn_ctrl_diarias;
				
				if (DominioAtualizacaoSaldo.I.equals(operacao)) {
					
					final List<FatEspelhoAih> eihs = getFatEspelhoAihDao().listarPorCth(cthSeq);
					
					if(eihs != null && !eihs.isEmpty()){
						for(FatEspelhoAih fea : eihs){
							fea.setIndBcoCapac("N");
							getEspelhoAihPersist().atualizar(fea, nomeMicrocomputador, dataFimVinculoServidor);
						}
						
					} else {
						throw new IllegalArgumentException("eih não pode ser nulo.");
					}
				}
				
				opc = -1;	// EXIT
			}
			
		} while (opc >= 0);
		
		return result;
	}

	
	private void atualizaFatBancoCapacidade(final DominioAtualizacaoSaldo operacao, final int diasUtilizado, final Integer vAno, final Integer vMes, final Integer codigoClinica, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException{
		final FatBancoCapacidade bncCap = getFatBancoCapacidadeDao().obterPorChavePrimaria(new FatBancoCapacidadeId(vAno, vMes, codigoClinica));
		
		int vUtilizado = bncCap.getUtilizado() != null ? bncCap.getUtilizado() : 0;
		
		if(DominioAtualizacaoSaldo.I.equals(operacao)){
			vUtilizado -= diasUtilizado;
			
		} else {
			vUtilizado += diasUtilizado;
		}
		
		bncCap.setUtilizado(Integer.valueOf(vUtilizado));
		getBancoCapacidadePersist().setComFlush(false);
		getBancoCapacidadePersist().atualizar(bncCap, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	protected FatkCth5RN getFatkCth5RN() {
		return fatkCth5RN;
	}
}

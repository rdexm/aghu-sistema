package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatPossibilidadeRealizadoDAO;
import br.gov.mec.aghu.faturamento.vo.RnFatcVerItprocexcVO;
import br.gov.mec.aghu.internacao.vo.FatItemContaHospitalarVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatPossibilidadeRealizado;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * <p>
 * Linhas: 137 <br/>
 * Cursores: 1 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 4 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 1 <br/>
 * Metodos externos: 1 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_PRZ_RN</code>
 * </p>
 * @author gandriotti
 *
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class FaturamentoFatkPrzRN extends AbstractFatDebugLogEnableRN {


@EJB
private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;

private static final Log LOG = LogFactory.getLog(FaturamentoFatkPrzRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatContasHospitalaresDAO fatContasHospitalaresDAO;

@Inject
private FatPossibilidadeRealizadoDAO fatPossibilidadeRealizadoDAO;

@Inject
private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;
	private static final long serialVersionUID = 129251531979082383L;

	/**
	 * <p>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 137 <br/>
	 * Cursores: 1 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 4 tabelas <br/>
	 * Alteracoes: 0 tabelas <br/>
	 * Metodos externos: 1 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_PRZC_VER_POSSIB</code>
	 * </p>
	 * @param pCthSeq 
	 * @param pIphPhoRealiz 
	 * @param pIphRealiz 
	 * @return 
	 * @throws BaseException 
	 * @see FatContasHospitalares
	 * @see FatPossibilidadeRealizado
	 * @see FatItemContaHospitalar
	 * 
	 * @see #getFatkSusRn()
	 */
	public boolean rnPrzcVerPossib(Integer pCthSeq, Short pIphPhoRealiz, Integer pIphRealiz) throws BaseException {
		Boolean vPossibNaConta = true;
		Short vPhoPossibilita = null;
		Integer vIphPossibilita = null;
		Boolean vItemPossibNaConta = null;
		Integer vPhiSeq = null;
		Short vQuantidade = null;
		Short vCspCnvCodigo = null;
		Byte vCspSeq = null;

		FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		FatPossibilidadeRealizadoDAO fatPossibilidadeRealizadoDAO = getFatPossibilidadeRealizadoDAO();
		FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN = getVerificacaoFaturamentoSusRN();

		// Busca dados da conta
		FatContasHospitalares contasHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);

		if (contasHospitalar != null && contasHospitalar.getConvenioSaudePlano() != null) {
			FatConvenioSaudePlano fatConvenioSaudePlano = contasHospitalar.getConvenioSaudePlano();
			vCspCnvCodigo = fatConvenioSaudePlano.getId().getCnvCodigo();
			vCspSeq = fatConvenioSaudePlano.getId().getSeq();
		}

		// Busca o numero de possibilidades
		List<Integer> possibilidades = fatPossibilidadeRealizadoDAO.listarPossibilidadesDistinct(pIphPhoRealiz, pIphRealiz);

		if (possibilidades != null && !possibilidades.isEmpty()) {
			for(Integer vNroPossibilidade : possibilidades){

				vPossibNaConta = true;

				// Verifica existencia de possibilidade
				final List<FatPossibilidadeRealizado> possibilidadesRealizados = fatPossibilidadeRealizadoDAO.listarPossibilidadesRealizados(pIphPhoRealiz, pIphRealiz, vNroPossibilidade);
				if (possibilidadesRealizados != null && !possibilidadesRealizados.isEmpty()) {
					for(FatPossibilidadeRealizado fatPossibilidadeRealizado : possibilidadesRealizados){
						vPhoPossibilita = fatPossibilidadeRealizado.getId().getIphPhoSeqPossibilita();
						vIphPossibilita = fatPossibilidadeRealizado.getId().getIphSeqPossibilita();

						if (vPossibNaConta) {
							vItemPossibNaConta = false;

							// Busca os itens da conta
							List<FatItemContaHospitalarVO> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthSituacoes(pCthSeq, DominioSituacaoItenConta.A);
							if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
								for(FatItemContaHospitalarVO itemContaHospitalar : itensContaHospitalar){
									vPhiSeq = itemContaHospitalar.getPhiSeq();
									vQuantidade = itemContaHospitalar.getQuantidadeRealizada();

									List<RnFatcVerItprocexcVO> rnFatcVerItprocexcVOList = verificacaoFaturamentoSusRN.verificarItemProcHosp(vPhiSeq, vQuantidade, vCspCnvCodigo, vCspSeq);
									if (rnFatcVerItprocexcVOList != null) {
										for (RnFatcVerItprocexcVO rnFatcVerItprocexcVO : rnFatcVerItprocexcVOList) {
											if(rnFatcVerItprocexcVO.getPhoSeq().equals(vPhoPossibilita) && rnFatcVerItprocexcVO.getSeq().equals(vIphPossibilita)){
												vItemPossibNaConta = true;
												break;
											}	
										}
									}
									// Se o item da possibilidade OK, verifica proximo
									if (vItemPossibNaConta) {
										break;
									}
								}
							}
						}

						// Passou todos os itens da conta e item possib nao esta
						if (!vItemPossibNaConta) {
							vPossibNaConta = false;
							break;
						}
					}
				}

				// Passou todos os itens da possib e todos na conta
				if (vItemPossibNaConta) {
					return true;
				}
			}
		}

		if(vPossibNaConta){
			logar("Marina - final TRUE");
			return true;
		} else {
			logar("Marina -final FALSE");
			return false;
		}
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}
	
	protected FatPossibilidadeRealizadoDAO getFatPossibilidadeRealizadoDAO() {
		return fatPossibilidadeRealizadoDAO;
	}
	
	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}
	
	protected VerificacaoFaturamentoSusRN getVerificacaoFaturamentoSusRN() {
		return verificacaoFaturamentoSusRN;
	}
	
}

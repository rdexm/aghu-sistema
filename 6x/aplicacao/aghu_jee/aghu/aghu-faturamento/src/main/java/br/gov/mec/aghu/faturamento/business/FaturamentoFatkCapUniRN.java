package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.vo.RnCapcCboProcResVO;
import br.gov.mec.aghu.model.AacCaracteristicaGrade;
import br.gov.mec.aghu.model.AacCaracteristicaGradeId;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.CursorBuscaCboVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FaturamentoFatkCapUniRN extends AbstractFatDebugLogEnableRN {


@EJB
private CaracteristicaItemProcedimentoHospitalarRN caracteristicaItemProcedimentoHospitalarRN;

private static final Log LOG = LogFactory.getLog(FaturamentoFatkCapUniRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IExamesFacade examesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4209469545989926281L;

	/**
	 * ORADB Function RN_CAPC_CBO_PROC_RES
	 * 
	 * @param pSerMatricula
	 * @param pSerVinCodigo
	 * @param pSoeSeq
	 * @param pIseSeqp
	 * @param pConNumero
	 * @param pCrgSeq
	 * @param pIphPhoSeq
	 * @param pIphSeq
	 * @param pDtRealizacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public RnCapcCboProcResVO rnCapcCboProcRes(Integer pSerMatricula,
			Short pSerVinCodigo, Integer pSoeSeq, Short pIseSeqp,
			Integer pConNumero, Integer pCrgSeq, Short pIphPhoSeq,
			Integer pIphSeq, Date pDtRealizacao, 
			List<Short> resultSeqTipoInformacaoShort, Date ultimoDiaMes) throws ApplicationBusinessException {

		RnCapcCboProcResVO retorno = new RnCapcCboProcResVO();

		Integer vContaCbo = 0;
		Boolean vNaoConsiste = Boolean.FALSE;
		Integer vTctTipoApac = 0;
		Integer vPesCodigo = null;
		Short vVinCodigo = null;
		Integer vMatricula = null;
		String vItemCbo = null;
		String vCboPrinc = null;
		AghParametros param = null;

		logar("..............BUSCA CBO COMP..............");

		if (pSerMatricula != null && pSerVinCodigo != null) {
			RapServidores servidor = getRegistroColaboradorFacade()
					.buscaServidor(
							new RapServidoresId(pSerMatricula, pSerVinCodigo));
			vPesCodigo = servidor.getPessoaFisica().getCodigo();
			vVinCodigo = servidor.getId().getVinCodigo();
			vMatricula = servidor.getId().getMatricula();
		}
		else if (pSoeSeq != null && pIseSeqp != null) { 
			final String valorParametroSitLiberado = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
			RapServidores servidor = getRegistroColaboradorFacade().buscarRapServidorDeAelExtratoItemSolicitacao(pSoeSeq, pIseSeqp, valorParametroSitLiberado);
			 if (servidor != null) {
				 vPesCodigo = servidor.getPessoaFisica().getCodigo();
				 vVinCodigo = servidor.getId().getVinCodigo();
				 vMatricula = servidor.getId().getMatricula();
			 }
			 param = buscarAghParametro(AghuParametrosEnum.P_PES_CODIGO_USUARIO);
			 String[] vetorTipoCns = param.getVlrTexto().split("\\,");
			 if (servidor != null && servidor.getPessoaFisica() != null && (servidor.getPessoaFisica().getCodigo().equals(Integer.valueOf(vetorTipoCns[0])) || servidor.getPessoaFisica().getCodigo().equals(Integer.valueOf(vetorTipoCns[1])))) { //Mariana Jobim Wilson e Usuário Imuno 
				param = buscarAghParametro(AghuParametrosEnum.P_PES_CODIGO_RESPONSAVEL);
				vPesCodigo = param.getVlrNumerico().intValue(); // -- Monica
																// Kruger

				param = buscarAghParametro(AghuParametrosEnum.P_PES_VINCULO_RESPONSAVEL);
				vVinCodigo = param.getVlrNumerico().shortValue();

				param = buscarAghParametro(AghuParametrosEnum.P_PES_MATRICULA_RESPONSAVEL);
				vMatricula = param.getVlrNumerico().intValue();
			}
		} else if (pConNumero != null) {
			List<AacGradeAgendamenConsultas> listaGradeAgendConsultas = getAmbulatorioFacade()
					.executaCursorGetCboExame(pConNumero);
			if (!listaGradeAgendConsultas.isEmpty()) {
				AacGradeAgendamenConsultas grade = listaGradeAgendConsultas
						.get(0);

				final String caracteristica = buscarVlrTextoAghParametro(AghuParametrosEnum.P_CARACTERISTICA_GRADE_PROFISSIONAL_CBO);
				AacCaracteristicaGrade aacCaract = getAmbulatorioFacade()
						.obterCaracteristicaGradePorChavePrimaria(
								new AacCaracteristicaGradeId(grade.getSeq(), caracteristica)); // "Obter CBO profissional"));
				
				if (aacCaract != null && grade.getProfServidor() != null) { // -- cbo do profissional
					vPesCodigo = grade.getProfServidor().getPessoaFisica().getCodigo();
					vVinCodigo = grade.getProfServidor().getId().getVinCodigo();
					vMatricula = grade.getProfServidor().getId().getMatricula();
					
				} else { // -- cbo do chefe da equipe
					vPesCodigo = grade.getEquipe().getProfissionalResponsavel().getPessoaFisica().getCodigo();
					vVinCodigo = grade.getEquipe().getProfissionalResponsavel().getId().getVinCodigo();
					vMatricula = grade.getEquipe().getProfissionalResponsavel().getId().getMatricula();
				}
			}
		} else if (pCrgSeq != null) {
			MbcProfCirurgias profCirurgia = getBlocoCirurgicoFacade()
					.buscaRapServidorPorCrgSeqEIndResponsavel(pCrgSeq,DominioSimNao.S);
			if (profCirurgia != null) {
				vPesCodigo = profCirurgia.getServidorPuc().getPessoaFisica().getCodigo();
				vVinCodigo = profCirurgia.getServidorPuc().getId().getVinCodigo();
				vMatricula = profCirurgia.getServidorPuc().getId().getMatricula();
			}
		}

		// -- Marina 16/11/2008
		final List<CursorBuscaCboVO> listaCbo = getRegistroColaboradorFacade()
				.listarPessoaTipoInformacoes(vPesCodigo, pDtRealizacao, resultSeqTipoInformacaoShort, ultimoDiaMes);
		if (listaCbo.size() == 0) {
			logar("##### AGHU - listaCbo = 0");
		}
		for (CursorBuscaCboVO rBuscaCbos : listaCbo) {
			logar("r_get_valor_cbo.seq: {0}", rBuscaCbos.getTiiSeq());
			// SUBSTR(pii.valor,1,6) valor Criou-se um get para o substring...
			logar("r_get_valor_cbo.valor: {0}", rBuscaCbos.getValorSubs(6));

			vContaCbo++;
			if (Integer.valueOf(1).equals(vContaCbo)) {
				vCboPrinc = rBuscaCbos.getValorSubs(6);
			}
			vItemCbo = null;

			final FatProcedimentoCbo procedimentoCbo = getFatProcedimentoCboDAO()
					.obterFatProcedimentoCbo(pIphPhoSeq, pIphSeq,
							rBuscaCbos.getValorSubs(6),
							DateUtil.truncaData(pDtRealizacao));

			if (procedimentoCbo == null) {
				logar("##### AGHU - procedimentoCbo == null");
			}

			if (procedimentoCbo != null) {
				retorno.setRetorno(procedimentoCbo.getCbo().getCodigo());
				logar("achou {0}", retorno.getRetorno());
				return retorno;
			}
		}

		logar("v_item_cbo: " + vItemCbo);
		logar("v_conta_cbo: " + vContaCbo);
		
		if (vContaCbo.equals(0)) {
			retorno.setpErro("NTC");
			retorno.setpSerVinCodigo(vVinCodigo);
			retorno.setpSerMatr(vMatricula);

			return retorno; // -- insere log erros profissional sem cbo na rotina que chamou
		}

		if (vItemCbo == null) {
			vItemCbo = vCboPrinc;
			vTctTipoApac = getTipoCaracteristicaItemRN().obterTipoCaractItemSeq(DominioFatTipoCaractItem.NAO_CONSISTE_CBO);

			FatCaractItemProcHosp caractItemProcHosp = getCaracteristicaItemProcedimentoHospitalarRN()
					.obterCaracteristicaProcHospPorId(new FatCaractItemProcHospId(pIphPhoSeq, pIphSeq,vTctTipoApac));

			if (caractItemProcHosp == null) {
				vNaoConsiste = Boolean.TRUE;
			} else {
				if (caractItemProcHosp.getValorChar().equals('S')) {
					vNaoConsiste = Boolean.FALSE;	
				} else {
					vNaoConsiste = Boolean.TRUE;
				}
			}	
			if (Boolean.FALSE.equals(vNaoConsiste)) { // -- não tem característica
				retorno.setpErro("INC");
				retorno.setpSerVinCodigo(vVinCodigo);
				retorno.setpSerMatr(vMatricula);
				retorno.setRetorno(vItemCbo);
				// -- insere log erros cbo prof incompatível com cbo procedimento
			} else if(caractItemProcHosp != null && caractItemProcHosp.getValorChar()!=null){
				retorno.setRetorno(caractItemProcHosp.getValorChar());
			}
		}

		return retorno;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	protected CaracteristicaItemProcedimentoHospitalarRN getCaracteristicaItemProcedimentoHospitalarRN() {
		return caracteristicaItemProcedimentoHospitalarRN;
	}
}

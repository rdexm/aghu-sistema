package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * @author aghu
 *
 */
@Stateless
public class MbcfEscolheFatRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcfEscolheFatRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;
	
	@EJB
	private IFaturamentoFacade iFaturamentoFacade;
	
	
	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private ValorCadastralProcedimentoSusRN valorCadastralProcedimentoSusRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private DataCompetenciaRegistroCirurgiaRealizadoRN dataCompetenciaRegistroCirurgiaRealizadoRN;

	private static final long serialVersionUID = 5977433293988331868L;
	
	/**
	 * PROCEDURE CGFK$QRY_PHI_FAT_PHI_VAPR_FK1  decode(:global.mbc$origem,'I',1,'A',2,0)
	 * @param origem
	 * @return
	 */
	public Byte getOrigemCodigo(DominioOrigemPacienteCirurgia origem) {
		Byte origemCodigo = null;
		if (origem.equals(DominioOrigemPacienteCirurgia.I)) {
			origemCodigo = 1;
		} else if (origem.equals(DominioOrigemPacienteCirurgia.A)) {
			origemCodigo = 2;
		} else {
			origemCodigo = 0;
		}
		return origemCodigo;
	}
	
	
	/**
	 * @ORADB CGFK$QRY_LOOKUP_DATA Query lookup data for the foreign key(s)
	 * @param listRetorno
	 * @param origem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<CirurgiaCodigoProcedimentoSusVO> calcularValorTotalEValorPerm (Integer pciSeq, Short espSeq, DominioOrigemPacienteCirurgia origem) throws ApplicationBusinessException {
		
		if(origem == DominioOrigemPacienteCirurgia.I){
			//Verifica se o timpo de procedimento eh faturado como ambulatorial, se for é alterado a origem para ambulatorio
			MbcProcedimentoCirurgicos procedimentoIsAmbulatorial = getBlocoCirurgicoFacade().obterProcedimentoLancaAmbulatorio(pciSeq);
			if(procedimentoIsAmbulatorial != null){
				List<AghCaractEspecialidades> possuiCaracteristica = getAghuFacade().pesquisarCaracteristicaEspecialidade(espSeq, DominioCaracEspecialidade.LANCA_ITEM_AMBULATORIO);
				if(possuiCaracteristica != null && possuiCaracteristica.size() > 0){
					 origem = DominioOrigemPacienteCirurgia.A; 
				}
			}
		}
		
		final Date valorDtComp = getDataCompetenciaRegistroCirurgiaRealizadoRN().getValorDataCompetencia(origem);
		final AghParametros grupoSUS = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		Byte cpgCphCspSeq = getOrigemCodigo(origem);
		
		List<Integer> phiSeqList = this.getFaturamentoFacade().
				buscarPhiSeqPorPciSeqOrigemPacienteCodigo(pciSeq, cpgCphCspSeq, grupoSUS.getVlrNumerico().shortValue());

		List<CirurgiaCodigoProcedimentoSusVO> listRetorno = getCursorFatAssocProcdporPhiSeqOrigemGrupo(origem, grupoSUS, phiSeqList, cpgCphCspSeq);
		
		// Percorre a lista final adicionando o valor total e valor perm
		for (CirurgiaCodigoProcedimentoSusVO vo : listRetorno) {
			List<FatVlrItemProcedHospComps> listaVlrItemProcedHospComps = this.getFaturamentoFacade().pesquisarVlrItemProcedHospCompsPorIphSeqDtComp(vo.getIphSeq(),
					vo.getIphPhoSeq(), valorDtComp);

			BigDecimal valorTotal = BigDecimal.ZERO;
			BigDecimal valorPerm = BigDecimal.ZERO;
			
			if (listaVlrItemProcedHospComps != null && !listaVlrItemProcedHospComps.isEmpty()) {
				
				FatVlrItemProcedHospComps itemSmm = listaVlrItemProcedHospComps.get(0);

				valorTotal = getValorCadastralProcedimentoSusRN()
						.getValorCadastralProcedimentoSus(origem,
								itemSmm.getVlrServHospitalar(),
								itemSmm.getVlrServProfissional(),
								itemSmm.getVlrSadt(),
								itemSmm.getVlrProcedimento(),
								itemSmm.getVlrAnestesista());
				
				valorPerm = (new BigDecimal(itemSmm
						.getFatItensProcedHospitalar()
						.getQuantDiasFaturamento() != null ? itemSmm
						.getFatItensProcedHospitalar()
						.getQuantDiasFaturamento() : 0));
			}
			
			vo.setValorTotal(valorTotal);
			vo.setValorPerm(valorPerm);
		}
		
		return listRetorno;
	}
	
	/**
	 * @ORADB PROCEDURE CGFK$QRY_PHI_FAT_PHI_VAPR_FK1 - FORM MBCF_ESCOLHER_FAT
	 * @param origem
	 * @param grupoSUS
	 * @param phiSeqList
	 * @return List<CirurgiaCodigoProcedimentoSusVO>
	 */
	private List<CirurgiaCodigoProcedimentoSusVO> getCursorFatAssocProcdporPhiSeqOrigemGrupo(DominioOrigemPacienteCirurgia origem, final AghParametros grupoSUS, List<Integer> phiSeqList,  Byte cpgCphCspSeq) {
		List<CirurgiaCodigoProcedimentoSusVO> listRetorno = new ArrayList<CirurgiaCodigoProcedimentoSusVO>();
		
		for (Integer phiSeq : phiSeqList) {

			List<CirurgiaCodigoProcedimentoSusVO> cirurgiaCodigoProcedimentoSusVOs = this
					.getFaturamentoFacade()
					.getCursorFatAssocProcdporPhiSeqOrigemGrupo(phiSeq, grupoSUS.getVlrNumerico().shortValue(), cpgCphCspSeq);
			
			CirurgiaCodigoProcedimentoSusVO cirurgiaCodigoProcedimentoSusVO = cirurgiaCodigoProcedimentoSusVOs.get(0);
			
			cirurgiaCodigoProcedimentoSusVO.setPhiSeq(phiSeq);

			listRetorno.add(cirurgiaCodigoProcedimentoSusVO);	
		}
		
		return listRetorno;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return this.iBlocoCirurgicoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected ValorCadastralProcedimentoSusRN  getValorCadastralProcedimentoSusRN(){
		return valorCadastralProcedimentoSusRN;
	}
	
	protected DataCompetenciaRegistroCirurgiaRealizadoRN getDataCompetenciaRegistroCirurgiaRealizadoRN() {
		return dataCompetenciaRegistroCirurgiaRealizadoRN;
	}

}

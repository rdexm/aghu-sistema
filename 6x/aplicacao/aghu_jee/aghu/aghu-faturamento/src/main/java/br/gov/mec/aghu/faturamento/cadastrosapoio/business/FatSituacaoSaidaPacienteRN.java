package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.faturamento.dao.CntaConvDAO;
//import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPacienteId;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatSituacaoSaidaPacienteRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = -8392628193048242109L;
	
	private static final Log LOG = LogFactory.getLog(FatSituacaoSaidaPacienteRN.class);
	
	@Inject
	private FatSituacaoSaidaPacienteDAO fatSituacaoSaidaPacienteDAO;
	
	@Inject
	private FatMotivoSaidaPacienteDAO fatMotivoSaidaPacienteDAO;
	
//	@Inject
//	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO;
	
	@Inject
	private CntaConvDAO cntaConvDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum FatSituacaoSaidaPacienteExceptionCode implements BusinessExceptionCode {
		M5_IDADE_MAXIMA_MENOR_IGUAL_IDADE_MINIMA, 
		M6_IDADE_MINIMA_NEGATIVA, 
		M7_IDADE_MAXIMA_NEGATIVA, 
		OFG_00005_M2, 
		OFG_00005_M3, 
		OFG_00005_M4,
		ERRO_REMOVER_SITUACAO_SAIDA_PACIENTE_COM_DEPENDENCIA,
		ERRO_REMOVER_SITUACAO_SAIDA_PACIENTE
	}
	
	public List<FatSituacaoSaidaPaciente> recuperarListaPaginada(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoSaidaPaciente filtro) {
		
		return this.fatSituacaoSaidaPacienteDAO.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, filtro);
	}

	public Long recuperarCount(final FatMotivoSaidaPaciente filtro) {

		return this.fatSituacaoSaidaPacienteDAO.recuperarCount(filtro);
	}
	
	/**
	 * Método responsável pela persistencia/alteração da entidade.
	 *  
	 * @param entity {@link FatSituacaoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void gravar(FatSituacaoSaidaPaciente entity) throws ApplicationBusinessException {
		
		final short zero = 0;
		
		if (entity != null && entity.getIdadeMinima() != null && entity.getIdadeMaxima() != null) {

			if (entity.getIdadeMinima().compareTo(zero) < 0) {
				
				throw new ApplicationBusinessException(FatSituacaoSaidaPacienteExceptionCode.M6_IDADE_MINIMA_NEGATIVA);
			}
			
			if (entity.getIdadeMaxima().compareTo(zero) < 0) {
				
				throw new ApplicationBusinessException(FatSituacaoSaidaPacienteExceptionCode.M7_IDADE_MAXIMA_NEGATIVA);
			}
			
			if (entity.getIdadeMaxima().compareTo(entity.getIdadeMinima()) <= 0) {
		
				throw new ApplicationBusinessException(FatSituacaoSaidaPacienteExceptionCode.M5_IDADE_MAXIMA_MENOR_IGUAL_IDADE_MINIMA);
			} 
				
			if (entity.getId() != null && entity.getId().getMspSeq() != null) {
				
				entity.setMotivoSaidaPaciente(this.fatMotivoSaidaPacienteDAO.obterPorChavePrimaria(entity.getId().getMspSeq()));
				
				if (entity.getId().getSeq() != null) {
					
					this.fatSituacaoSaidaPacienteDAO.atualizar(entity);
					
				} else {
					
					entity.getId().setSeq(this.fatSituacaoSaidaPacienteDAO.obterMaxSeqFatSituacaoSaidaPaciente(entity.getId().getMspSeq()));
					
					this.fatSituacaoSaidaPacienteDAO.persistir(entity);
				}
			}
		}
	}

	/**
	 * Método responsável pela exclusão da entidade.
	 *  
	 * @param fatSituacaoSaidaPacienteId {@link FatSituacaoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void remover(FatSituacaoSaidaPacienteId fatSituacaoSaidaPacienteId) throws ApplicationBusinessException {
		
		if (fatSituacaoSaidaPacienteId != null) {
		
			FatSituacaoSaidaPaciente entity = this.fatSituacaoSaidaPacienteDAO.obterPorChavePrimaria(fatSituacaoSaidaPacienteId);
		
			if (entity != null) {
			
				if (possuiFatContasHospitalares(entity)) {
					
					throw new ApplicationBusinessException(FatSituacaoSaidaPacienteExceptionCode.OFG_00005_M2);
				}
				
				if (possuiVFatContaHospitalarPac(entity)) {
					
					throw new ApplicationBusinessException(FatSituacaoSaidaPacienteExceptionCode.OFG_00005_M3);
				}
				
				if (isHCPA() && possuiCntaConv(entity)) {
					
					throw new ApplicationBusinessException(FatSituacaoSaidaPacienteExceptionCode.OFG_00005_M4);
				}
				
				try {
					
					this.fatSituacaoSaidaPacienteDAO.remover(entity);
					this.fatSituacaoSaidaPacienteDAO.flush();
					
				} catch (Exception e) {
				
					if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
						
						ConstraintViolationException ecv = (ConstraintViolationException) e.getCause();
						
						if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),	"FAT_CTH_SIA_FK1")) {
							throw new ApplicationBusinessException(
									FatSituacaoSaidaPacienteExceptionCode.OFG_00005_M2);
						}
						
						throw new ApplicationBusinessException(
								FatSituacaoSaidaPacienteExceptionCode.ERRO_REMOVER_SITUACAO_SAIDA_PACIENTE_COM_DEPENDENCIA);
					}
					throw new ApplicationBusinessException(
							FatSituacaoSaidaPacienteExceptionCode.ERRO_REMOVER_SITUACAO_SAIDA_PACIENTE);
				}
			}
		}
	}
	
	/**
	 * Consulta {@link FatContasHospitalares} vinculadas ao {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param entity {@link FatSituacaoSaidaPaciente}
	 * @return <code>true</code> se possui registro vinculado ou <code>false</code> caso contrário.
	 */
	private boolean possuiFatContasHospitalares(FatSituacaoSaidaPaciente entity) {

//		List<FatContasHospitalares> contasHospitalares = this.fatContasHospitalaresDAO.listarFatContasHospitalaresPorFatSituacaoSaidaPaciente(entity);
//		
//		if (contasHospitalares != null && !contasHospitalares.isEmpty()) {
//			
//			return true;
//		}
		
		return false;
	}
	
	/**
	 * Consulta {@link VFatContaHospitalarPac} vinculadas ao {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param entity {@link FatSituacaoSaidaPaciente}
	 * @return <code>true</code> se possui registro vinculado ou <code>false</code> caso contrário.
	 */
	private boolean possuiVFatContaHospitalarPac(FatSituacaoSaidaPaciente entity) {

		List<VFatContaHospitalarPac> contasHospitalaresPac = this.vFatContaHospitalarPacDAO.listarVFatContaHospitalarPacPorFatSituacaoSaidaPaciente(entity);
		
		if (contasHospitalaresPac != null && !contasHospitalaresPac.isEmpty()) {
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retorna lista de {@link CntaConv} passando como parametro {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param entity {@link FatSituacaoSaidaPaciente}
	 * @return <code>true</code> se possui registro vinculado ou <code>false</code> caso contrário.
	 */
	private boolean possuiCntaConv(FatSituacaoSaidaPaciente entity) {

		List<CntaConv> listaCntaConv = this.cntaConvDAO.listarCntaConvPorFatSituacaoSaidaPaciente(entity);
		
		if (listaCntaConv != null && !listaCntaConv.isEmpty()) {
			
			return true;
		}
		
		return false;
	}
}

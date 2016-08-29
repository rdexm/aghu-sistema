package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtGravidezAnterior;
import br.gov.mec.aghu.model.MpmObtGravidezAnteriorId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtGravidezAnteriorDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObtGravidezAnteriorON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterObtGravidezAnteriorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmObtGravidezAnteriorDAO mpmObtGravidezAnteriorDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3467879980204006472L;
	
	public enum ManterObtGravidezAnteriorONExceptionCode implements BusinessExceptionCode {

		ERRO_INSERIR_OBT_GRAVIDEZ_ANTERIOR,ERRO_ALTERAR_OBT_GRAVIDEZ_ANTERIOR;

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}
	
	/**
	 * Cria uma cópia de ObtGravidezAnterior
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void versionarObtGravidezAnterior(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmObtGravidezAnterior obtGravidezAnterior = this.getMpmObtGravidezAnteriorDAO().obterMpmObtGravidezAnterior(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (obtGravidezAnterior != null) {
			
			MpmObtGravidezAnterior novoObtGravidezAnterior = new MpmObtGravidezAnterior();
			novoObtGravidezAnterior.setMpmAltaSumarios(altaSumario);
			novoObtGravidezAnterior.setGravidezAnterior(obtGravidezAnterior.getGravidezAnterior());
			this.getMpmObtGravidezAnteriorDAO().persistir(novoObtGravidezAnterior);
			this.getMpmObtGravidezAnteriorDAO().flush();
			
		}
		
	}
	
	/**
	 * Remove ObtGravidezAnterior
	 * @param altaSumario
	 */
	public void removerObtGravidezAnterior(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		if(altaSumario!=null){
			MpmObtGravidezAnterior obtGravidezAnterior = this.getMpmObtGravidezAnteriorDAO().obterMpmObtGravidezAnterior(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
			
			if (obtGravidezAnterior != null) {
				
				this.getMpmObtGravidezAnteriorDAO().remover(obtGravidezAnterior);
				this.getMpmObtGravidezAnteriorDAO().flush();
			}
		}
	}
	
	public MpmObtGravidezAnterior obterMpmObtGravidezAnterior(MpmAltaSumario altaSumario) {
		Integer altanAtdSeq = null;
		Integer altanApaSeq = null;
		Short altanAsuSeqp = null;
		
		if(altaSumario!=null){
			altanAtdSeq = altaSumario.getId().getApaAtdSeq();
			altanApaSeq = altaSumario.getId().getApaSeq();
			altanAsuSeqp = altaSumario.getId().getSeqp();
		}
		
		return getMpmObtGravidezAnteriorDAO().obterMpmObtGravidezAnterior(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}
	
	
	/**
	 * Se o paciente for do Sexo Masculino a opção 'N' será uma constante
	 * @param dominioSexo sexo
	 * @param indicadorNecropsia indicador de necrópsia antigo
	 * @return
	 */
	public DominioSimNao obterIndicadorNecropsia(DominioSexo dominioSexo, DominioSimNao indicadorNecropsia){
		return dominioSexo.equals(DominioSexo.M) ? DominioSimNao.N : indicadorNecropsia;
	}
	
	public String gravarObtGravidezAnterior(MpmAltaSumario altaSumario, DominioSimNao indicadorNecropsia) throws ApplicationBusinessException {
		String confirmacao = null;
		
		if(altaSumario != null){

			Integer altanAtdSeq = altaSumario.getId().getApaAtdSeq();
			Integer altanApaSeq = altaSumario.getId().getApaSeq();
			Short altanAsuSeqp = altaSumario.getId().getSeqp();
			
			MpmObtGravidezAnterior obtGravidezAnterior = getMpmObtGravidezAnteriorDAO().obterMpmObtGravidezAnterior(altanAtdSeq, altanApaSeq, altanAsuSeqp);

			// Se o paciente for do Sexo Masculino a opção 'N' será uma constante
			indicadorNecropsia = obterIndicadorNecropsia(altaSumario.getPaciente().getSexo(), indicadorNecropsia);
			
			if (obtGravidezAnterior == null) {// Inserir Obito Necrópsia

				    obtGravidezAnterior = new MpmObtGravidezAnterior();
				    MpmObtGravidezAnteriorId id = new  MpmObtGravidezAnteriorId();
					id.setAsuApaAtdSeq(altanAtdSeq);
					id.setAsuApaSeq(altanApaSeq);
					id.setAsuSeqp(altanAsuSeqp);
					obtGravidezAnterior.setId(id);
					
					obtGravidezAnterior.setMpmAltaSumarios(altaSumario);
					
					obtGravidezAnterior.setGravidezAnterior(indicadorNecropsia);
					
					inserirObtGravidezAnterior(obtGravidezAnterior);
					
					confirmacao = "MENSAGEM_SUCESSO_INCLUSAO_OBT_GRAVIDEZ_ANTERIOR";
	

			} else { // Atualizar Obito Necrópsia
				obtGravidezAnterior.setMpmAltaSumarios(altaSumario);
				obtGravidezAnterior.setGravidezAnterior(indicadorNecropsia);
				atualizarObtGravidezAnterior(obtGravidezAnterior);
				confirmacao = "MENSAGEM_SUCESSO_ALTERACAO_OBT_GRAVIDEZ_ANTERIOR";
			}
		}
		return confirmacao;
	}
	
	/**
	 * Insere objeto MpmObitoNecropsia.
	 * 
	 * @param {MpmObitoNecropsia} obitoNecropsia
	 */
	public void inserirObtGravidezAnterior(MpmObtGravidezAnterior obtGravidezAnterior)
			throws ApplicationBusinessException {

		try {

			getMpmObtGravidezAnteriorDAO().persistir(obtGravidezAnterior);
			this.getMpmObtGravidezAnteriorDAO().flush();

		} catch (Exception e) {

			logError(e.getMessage(), e);
			ManterObtGravidezAnteriorONExceptionCode.ERRO_INSERIR_OBT_GRAVIDEZ_ANTERIOR
					.throwException(e);

		}

	}
	
	/**
	 * Atualiza objeto MpmObitoNecropsia.
	 * 
	 * @param {MpmObitoNecropsia} obitoNecropsia
	 */
	public void atualizarObtGravidezAnterior(MpmObtGravidezAnterior obtGravidezAnterior)
			throws ApplicationBusinessException {
		try {
			getMpmObtGravidezAnteriorDAO().atualizar(obtGravidezAnterior);
			this.getMpmObtGravidezAnteriorDAO().flush();
		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterObtGravidezAnteriorONExceptionCode.ERRO_ALTERAR_OBT_GRAVIDEZ_ANTERIOR
			.throwException(e);
		}
	}		
	
	protected MpmObtGravidezAnteriorDAO getMpmObtGravidezAnteriorDAO() {
		return mpmObtGravidezAnteriorDAO;
	}

	public boolean validaNenhumaGravidezAnteriorMasculina(MpmAltaSumarioId mpmAltaSumarioId) {
		MpmObtGravidezAnterior obtGravidezAnterior = getMpmObtGravidezAnteriorDAO().obterMpmObtGravidezAnterior(mpmAltaSumarioId.getApaAtdSeq(), mpmAltaSumarioId.getApaSeq(), mpmAltaSumarioId.getSeqp());

		if(obtGravidezAnterior != null){
			return DominioSimNao.N.equals(obtGravidezAnterior.getGravidezAnterior());
		}else{
			return true;
		}
		//return obtGravidezAnterior == null;
	}
	
	public boolean validaAoMenosUmaGravidezAnterior(
			MpmAltaSumarioId mpmAltaSumarioId) {
		MpmObtGravidezAnterior obtGravidezAnterior = getMpmObtGravidezAnteriorDAO()
				.obterMpmObtGravidezAnterior(mpmAltaSumarioId.getApaAtdSeq(),
						mpmAltaSumarioId.getApaSeq(),
						mpmAltaSumarioId.getSeqp());

		return obtGravidezAnterior != null;
	}

	public boolean validarMaisDeUmaGravidezAnterior(
			MpmAltaSumarioId mpmAltaSumarioId) {
		try {
			getMpmObtGravidezAnteriorDAO()
					.obterMpmObtGravidezAnterior(
							mpmAltaSumarioId.getApaAtdSeq(),
							mpmAltaSumarioId.getApaSeq(),
							mpmAltaSumarioId.getSeqp());
		} catch (HibernateException e) {
			logError("Exceção capturada: ", e);
			// ocorreu exception uniqueResult - if there is more than one
			// matching result
			return false;
		}
		return true;

	}

}

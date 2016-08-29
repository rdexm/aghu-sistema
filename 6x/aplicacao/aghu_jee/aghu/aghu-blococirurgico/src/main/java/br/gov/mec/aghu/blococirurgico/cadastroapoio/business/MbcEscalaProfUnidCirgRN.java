package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 *
 * 
 */
@Stateless
public class MbcEscalaProfUnidCirgRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcEscalaProfUnidCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;
	
	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1660648832327444424L;

	public enum MbcEscalaProfUnidCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00415, MBC_00416, MBC_00414, MENSAGEM_JA_EXISTE_ESCALA_PROFISSIONAL;
	}
	
	protected MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO() {
		return mbcEscalaProfUnidCirgDAO;
	}
	
	/**
	 * @ORADB MBCT_EPU_BRI
	 * 
	 * @param novoEscala
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void insert(MbcEscalaProfUnidCirg novoEscala) throws ApplicationBusinessException{
		preInsert(novoEscala);
		getMbcEscalaProfUnidCirgDAO().persistir(novoEscala);
	}
	
	public void preInsert(MbcEscalaProfUnidCirg novoEscala) throws ApplicationBusinessException{
		
		MbcEscalaProfUnidCirg escalaJaExistente = getMbcEscalaProfUnidCirgDAO().obterPorChavePrimaria(novoEscala.getId());
		if(escalaJaExistente != null){
			throw new ApplicationBusinessException(MbcEscalaProfUnidCirgRNExceptionCode.MENSAGEM_JA_EXISTE_ESCALA_PROFISSIONAL);
		}
		
		MbcCaracteristicaSalaCirg caract = mbcCaracteristicaSalaCirgDAO.obterPorChavePrimaria(novoEscala.getMbcCaracteristicaSalaCirg().getSeq());
		MbcSalaCirurgica salaCir = caract.getMbcSalaCirurgica() != null ? mbcSalaCirurgicaDAO.obterPorChavePrimaria(caract.getMbcSalaCirurgica().getId()) : null;
		MbcProfAtuaUnidCirgs profAtuUniCir = mbcProfAtuaUnidCirgsDAO.obterPorChavePrimaria(novoEscala.getMbcProfAtuaUnidCirgs().getId());
		
		if(salaCir != null && !DominioSituacao.A.equals(salaCir.getSituacao())) {
			throw new ApplicationBusinessException(MbcEscalaProfUnidCirgRNExceptionCode.MBC_00416);
		}
		
		if(novoEscala.getMbcCaracteristicaSalaCirg().getMbcSalaCirurgica().getId().getUnfSeq().intValue() != novoEscala.getMbcProfAtuaUnidCirgs().getId().getUnfSeq().intValue()){
			throw new ApplicationBusinessException(MbcEscalaProfUnidCirgRNExceptionCode.MBC_00415);
		}
		
		if(!DominioSituacao.A.equals(profAtuUniCir.getSituacao())) {
			throw new ApplicationBusinessException(MbcEscalaProfUnidCirgRNExceptionCode.MBC_00414);
		}
		
	}

	public void delete(MbcEscalaProfUnidCirg editarEscala) {
		MbcEscalaProfUnidCirg mbcEscalaProfUnidCirgOriginal = getMbcEscalaProfUnidCirgDAO().obterPorChavePrimaria(editarEscala.getId());
		getMbcEscalaProfUnidCirgDAO().remover(mbcEscalaProfUnidCirgOriginal);
	}
	
//	Melhoria para trazer os dominios de médicos primeiro na combo #35212
	public List<DominioFuncaoProfissional> listarFuncaoProfissional(){
		
		List<DominioFuncaoProfissional> listFuncaoPrincipal = new ArrayList<DominioFuncaoProfissional>();
		List<DominioFuncaoProfissional> listFuncaoSecundaria = new ArrayList<DominioFuncaoProfissional>();

		for (DominioFuncaoProfissional funcao : DominioFuncaoProfissional.values()) {
			if(funcao.getDescricao().toString().startsWith("Médico")){
				listFuncaoPrincipal.add(funcao);
			}else{
				listFuncaoSecundaria.add(funcao);
			}
		}
		listFuncaoPrincipal.addAll(listFuncaoSecundaria);
		
		return listFuncaoPrincipal;
	}
}

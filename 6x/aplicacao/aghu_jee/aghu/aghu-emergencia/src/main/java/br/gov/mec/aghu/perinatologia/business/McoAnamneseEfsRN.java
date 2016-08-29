package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsId;
import br.gov.mec.aghu.model.McoAnamneseEfsJn;
import br.gov.mec.aghu.perinatologia.dao.McoAnamneseEfsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAnamneseEfsJnDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class McoAnamneseEfsRN extends BaseBusiness {

	private static final long serialVersionUID = -1618380103887639727L;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoAnamneseEfsDAO anamneseEfsDAO;
	
	@Inject
	private McoAnamneseEfsJnDAO anamneseEfsJnDAO;
	
	@Inject
	private AacConsultasDAO consultasDAO;
	
	private enum McoAnamneseEfsRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_NULL, MENSAGEM_BCF;
	}

	public void persistir(McoAnamneseEfs mcoAnamneseEfs, Integer conNumero, Integer pacCodigo, Short seqp) throws BaseException{
		
		try {
			Validate.notNull(mcoAnamneseEfs);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(McoAnamneseEfsRNExceptionCode.ERRO_PERSISTIR_NULL);
		}
		
		if (mcoAnamneseEfs.getId() == null) {
			inserir(mcoAnamneseEfs, conNumero, pacCodigo, seqp);
		} else {
			atualizar(mcoAnamneseEfs);
		}
	}
	
	private void atualizar(McoAnamneseEfs mcoAnamneseEfs) {
		
		McoAnamneseEfs mcoAnamneseEfsOriginal = this.anamneseEfsDAO.obterOriginal(mcoAnamneseEfs);		
		mcoAnamneseEfs.setSerVinCodigo(usuario.getVinculo());
		mcoAnamneseEfs.setSerMatricula(usuario.getMatricula());		
		anamneseEfsDAO.atualizar(mcoAnamneseEfs);		
		this.posUpdate(mcoAnamneseEfs, mcoAnamneseEfsOriginal);
	}

	private void posUpdate(McoAnamneseEfs mcoAnamneseEfs, McoAnamneseEfs mcoAnamneseEfsOriginal) {
		/* #25123 - RN01 - Passo 7. * Caso algum registro tenha sido alterado, realiza inserção na journal mco_anamnese_efs_jn passando ‘UPD’ na jn_operation.*/
		//Divido para passar na regra de pmd
		if(verificarSeFoiModificado1(mcoAnamneseEfs, mcoAnamneseEfsOriginal) || verificarSeFoiModificado2(mcoAnamneseEfs, mcoAnamneseEfsOriginal)) {
			this.inserirJournalMcoAnamneseEfs(mcoAnamneseEfsOriginal, DominioOperacoesJournal.UPD);
		}		
	}

	private boolean verificarSeFoiModificado2(McoAnamneseEfs mcoAnamneseEfs, McoAnamneseEfs mcoAnamneseEfsOriginal) {
		return (CoreUtil.modificados(mcoAnamneseEfs.getAcv(), mcoAnamneseEfsOriginal.getAcv())
				|| CoreUtil.modificados(mcoAnamneseEfs.getAr(), mcoAnamneseEfsOriginal.getAr())
				|| CoreUtil.modificados(mcoAnamneseEfs.getObservacao(), mcoAnamneseEfsOriginal.getObservacao())
				|| CoreUtil.modificados(mcoAnamneseEfs.getCriadoEm(), mcoAnamneseEfsOriginal.getCriadoEm())
				|| CoreUtil.modificados(mcoAnamneseEfs.getDigSeq(), mcoAnamneseEfsOriginal.getDigSeq())
				|| CoreUtil.modificados(mcoAnamneseEfs.getCidSeq(), mcoAnamneseEfsOriginal.getCidSeq())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIndMovFetal(), mcoAnamneseEfsOriginal.getIndMovFetal())
				|| CoreUtil.modificados(mcoAnamneseEfs.getSerMatricula(), mcoAnamneseEfsOriginal.getSerMatricula())
				|| CoreUtil.modificados(mcoAnamneseEfs.getSerVinCodigo(), mcoAnamneseEfsOriginal.getSerVinCodigo())
				|| CoreUtil.modificados(mcoAnamneseEfs.getExFisicoGeral(), mcoAnamneseEfsOriginal.getExFisicoGeral())
				|| CoreUtil.modificados(mcoAnamneseEfs.getBatimentoCardiacoFetal2(), mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal2())
				|| CoreUtil.modificados(mcoAnamneseEfs.getPressaoSistRepouso(), mcoAnamneseEfsOriginal.getPressaoSistRepouso())
				|| CoreUtil.modificados(mcoAnamneseEfs.getPressaoDiastRepouso(), mcoAnamneseEfsOriginal.getPressaoDiastRepouso())
				|| CoreUtil.modificados(mcoAnamneseEfs.getMcoPlanoIniciaises(), mcoAnamneseEfsOriginal.getMcoPlanoIniciaises())
				|| CoreUtil.modificados(mcoAnamneseEfs.getConsulta(), mcoAnamneseEfsOriginal.getConsulta())
				|| CoreUtil.modificados(mcoAnamneseEfs.getDiagnostico(), mcoAnamneseEfsOriginal.getDiagnostico())
				|| CoreUtil.modificados(mcoAnamneseEfs.getPaciente(), mcoAnamneseEfsOriginal.getPaciente())
				|| CoreUtil.modificados(mcoAnamneseEfs.getBatimentoCardiacoFetal3(), mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal3())
				|| CoreUtil.modificados(mcoAnamneseEfs.getBatimentoCardiacoFetal4(), mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal4())
				|| CoreUtil.modificados(mcoAnamneseEfs.getBatimentoCardiacoFetal5(), mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal5())
				|| CoreUtil.modificados(mcoAnamneseEfs.getBatimentoCardiacoFetal6(), mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal6())
				|| CoreUtil.modificados(mcoAnamneseEfs.getFormaRuptura(), mcoAnamneseEfsOriginal.getFormaRuptura())
				|| CoreUtil.modificados(mcoAnamneseEfs.getDataHoraRompimento(), mcoAnamneseEfsOriginal.getDataHoraRompimento())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIndDthrIgnorada(), mcoAnamneseEfsOriginal.getIndDthrIgnorada())
				|| CoreUtil.modificados(mcoAnamneseEfs.getLiquidoAmniotico(), mcoAnamneseEfsOriginal.getLiquidoAmniotico())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIndOdorFetido(), mcoAnamneseEfsOriginal.getIndOdorFetido()));
	}
	
	private boolean verificarSeFoiModificado1(McoAnamneseEfs mcoAnamneseEfs, McoAnamneseEfs mcoAnamneseEfsOriginal) {
		return (CoreUtil.modificados(mcoAnamneseEfs.getDthrConsulta(), mcoAnamneseEfsOriginal.getDthrConsulta())
				|| CoreUtil.modificados(mcoAnamneseEfs.getMotivo(), mcoAnamneseEfsOriginal.getMotivo())
				|| CoreUtil.modificados(mcoAnamneseEfs.getPressaoArtSistolica(), mcoAnamneseEfsOriginal.getPressaoArtSistolica())
				|| CoreUtil.modificados(mcoAnamneseEfs.getPressaoArtDiastolica(), mcoAnamneseEfsOriginal.getPressaoArtDiastolica())
				|| CoreUtil.modificados(mcoAnamneseEfs.getFreqCardiaca(), mcoAnamneseEfsOriginal.getFreqCardiaca())
				|| CoreUtil.modificados(mcoAnamneseEfs.getFreqRespiratoria(), mcoAnamneseEfsOriginal.getFreqRespiratoria())
				|| CoreUtil.modificados(mcoAnamneseEfs.getTemperatura(), mcoAnamneseEfsOriginal.getTemperatura())
				|| CoreUtil.modificados(mcoAnamneseEfs.getEdema(), mcoAnamneseEfsOriginal.getEdema())
				|| CoreUtil.modificados(mcoAnamneseEfs.getAlturaUterina(), mcoAnamneseEfsOriginal.getAlturaUterina())
				|| CoreUtil.modificados(mcoAnamneseEfs.getDinamicaUterina(), mcoAnamneseEfsOriginal.getDinamicaUterina())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIntensidadeDinUterina(), mcoAnamneseEfsOriginal.getIntensidadeDinUterina())	
				|| CoreUtil.modificados(mcoAnamneseEfs.getBatimentoCardiacoFetal(), mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIndAcelTrans(), mcoAnamneseEfsOriginal.getIndAcelTrans())
				|| CoreUtil.modificados(mcoAnamneseEfs.getSitFetal(), mcoAnamneseEfsOriginal.getSitFetal())
				|| CoreUtil.modificados(mcoAnamneseEfs.getExameEspecular(), mcoAnamneseEfsOriginal.getExameEspecular())
				|| CoreUtil.modificados(mcoAnamneseEfs.getPosicaoCervice(), mcoAnamneseEfsOriginal.getPosicaoCervice())
				|| CoreUtil.modificados(mcoAnamneseEfs.getEspessuraCervice(), mcoAnamneseEfsOriginal.getEspessuraCervice())
				|| CoreUtil.modificados(mcoAnamneseEfs.getApagamento(), mcoAnamneseEfsOriginal.getApagamento())
				|| CoreUtil.modificados(mcoAnamneseEfs.getDilatacao(), mcoAnamneseEfsOriginal.getDilatacao())
				|| CoreUtil.modificados(mcoAnamneseEfs.getApresentacao(), mcoAnamneseEfsOriginal.getApresentacao())
				|| CoreUtil.modificados(mcoAnamneseEfs.getPlanoDelee(), mcoAnamneseEfsOriginal.getPlanoDelee())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIndPromontorioAcessivel(), mcoAnamneseEfsOriginal.getIndPromontorioAcessivel())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIndEspCiaticaSaliente(), mcoAnamneseEfsOriginal.getIndEspCiaticaSaliente())
				|| CoreUtil.modificados(mcoAnamneseEfs.getIndSubPubicoMenor90(), mcoAnamneseEfsOriginal.getIndSubPubicoMenor90()));
	}

	private void inserirJournalMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfsOriginal, DominioOperacoesJournal operacao) {
		McoAnamneseEfsJn mcoAnamneseEfsJn = BaseJournalFactory.getBaseJournal(operacao, McoAnamneseEfsJn.class, usuario.getLogin());
		
		McoAnamneseEfsId idOriginal = mcoAnamneseEfsOriginal.getId();		
		mcoAnamneseEfsJn.setGsoPacCodigo(idOriginal.getGsoPacCodigo());
		mcoAnamneseEfsJn.setGsoSeqp(idOriginal.getGsoSeqp());
		mcoAnamneseEfsJn.setConNumero(idOriginal.getConNumero());
		mcoAnamneseEfsJn.setDthrConsulta(mcoAnamneseEfsOriginal.getDthrConsulta());
		mcoAnamneseEfsJn.setMotivo(mcoAnamneseEfsOriginal.getMotivo());
		mcoAnamneseEfsJn.setPressaoArtSistolica(mcoAnamneseEfsOriginal.getPressaoArtSistolica());
		mcoAnamneseEfsJn.setPressaoArtDiastolica(mcoAnamneseEfsOriginal.getPressaoArtDiastolica());
		mcoAnamneseEfsJn.setFreqCardiaca(mcoAnamneseEfsOriginal.getFreqCardiaca());
		mcoAnamneseEfsJn.setFreqRespiratoria(mcoAnamneseEfsOriginal.getFreqRespiratoria());
		mcoAnamneseEfsJn.setTemperatura(mcoAnamneseEfsOriginal.getTemperatura());
		mcoAnamneseEfsJn.setEdema(mcoAnamneseEfsOriginal.getEdema());
		mcoAnamneseEfsJn.setAlturaUterina(mcoAnamneseEfsOriginal.getAlturaUterina());
		mcoAnamneseEfsJn.setDinamicaUterina(mcoAnamneseEfsOriginal.getDinamicaUterina());
		mcoAnamneseEfsJn.setIntensidadeDinUterina(mcoAnamneseEfsOriginal.getIntensidadeDinUterina()); 
		mcoAnamneseEfsJn.setBatimentoCardiacoFetal(mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal());
		mcoAnamneseEfsJn.setIndAcelTrans(mcoAnamneseEfsOriginal.getIndAcelTrans());
		mcoAnamneseEfsJn.setSitFetal(mcoAnamneseEfsOriginal.getSitFetal());
		mcoAnamneseEfsJn.setExameEspecular(mcoAnamneseEfsOriginal.getExameEspecular());
		mcoAnamneseEfsJn.setPosicaoCervice(mcoAnamneseEfsOriginal.getPosicaoCervice());
		mcoAnamneseEfsJn.setEspessuraCervice(mcoAnamneseEfsOriginal.getEspessuraCervice());
		mcoAnamneseEfsJn.setApagamento(mcoAnamneseEfsOriginal.getApagamento());
		mcoAnamneseEfsJn.setDilatacao(mcoAnamneseEfsOriginal.getDilatacao());
		mcoAnamneseEfsJn.setApresentacao(mcoAnamneseEfsOriginal.getApresentacao());
		mcoAnamneseEfsJn.setPlanoDelee(mcoAnamneseEfsOriginal.getPlanoDelee());
		mcoAnamneseEfsJn.setIndPromontorioAcessivel(mcoAnamneseEfsOriginal.getIndPromontorioAcessivel());
		mcoAnamneseEfsJn.setIndEspCiaticaSaliente(mcoAnamneseEfsOriginal.getIndEspCiaticaSaliente());
		mcoAnamneseEfsJn.setIndSubPubicoMenor90(mcoAnamneseEfsOriginal.getIndSubPubicoMenor90());
		mcoAnamneseEfsJn.setAcv(mcoAnamneseEfsOriginal.getAcv());
		mcoAnamneseEfsJn.setAr(mcoAnamneseEfsOriginal.getAr());
		mcoAnamneseEfsJn.setObservacao(mcoAnamneseEfsOriginal.getObservacao());
		mcoAnamneseEfsJn.setCriadoEm(mcoAnamneseEfsOriginal.getCriadoEm());
		mcoAnamneseEfsJn.setDigSeq(mcoAnamneseEfsOriginal.getDigSeq());
		mcoAnamneseEfsJn.setCidSeq(mcoAnamneseEfsOriginal.getCidSeq());
		mcoAnamneseEfsJn.setIndMovFetal(mcoAnamneseEfsOriginal.getIndMovFetal());
		mcoAnamneseEfsJn.setSerMatricula(mcoAnamneseEfsOriginal.getSerMatricula());
		mcoAnamneseEfsJn.setSerVinCodigo(mcoAnamneseEfsOriginal.getSerVinCodigo());
		mcoAnamneseEfsJn.setExFisicoGeral(mcoAnamneseEfsOriginal.getExFisicoGeral());
		mcoAnamneseEfsJn.setBatimentoCardiacoFetal2(mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal2());
		mcoAnamneseEfsJn.setPressaoSistRepouso(mcoAnamneseEfsOriginal.getPressaoSistRepouso());
		mcoAnamneseEfsJn.setPressaoDiastRepouso(mcoAnamneseEfsOriginal.getPressaoDiastRepouso());
		mcoAnamneseEfsJn.setDigSeq(mcoAnamneseEfsOriginal.getDiagnostico() != null ? mcoAnamneseEfsOriginal.getDiagnostico().getSeq() : null);
		mcoAnamneseEfsJn.setCidSeq(mcoAnamneseEfsOriginal.getCidSeq());
		mcoAnamneseEfsJn.setBatimentoCardiacoFetal3(mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal3());
		mcoAnamneseEfsJn.setBatimentoCardiacoFetal4(mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal3());
		mcoAnamneseEfsJn.setBatimentoCardiacoFetal5(mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal3());
		mcoAnamneseEfsJn.setBatimentoCardiacoFetal6(mcoAnamneseEfsOriginal.getBatimentoCardiacoFetal3());
		mcoAnamneseEfsJn.setFormaRuptura(mcoAnamneseEfsOriginal.getFormaRuptura());
		mcoAnamneseEfsJn.setDataHoraRompimento(mcoAnamneseEfsOriginal.getDataHoraRompimento());
		mcoAnamneseEfsJn.setIndDthrIgnorada(mcoAnamneseEfsOriginal.getIndDthrIgnorada());
		mcoAnamneseEfsJn.setLiquidoAmniotico(mcoAnamneseEfsOriginal.getLiquidoAmniotico());
		mcoAnamneseEfsJn.setIndOdorFetido(mcoAnamneseEfsOriginal.getIndOdorFetido());		
		
		anamneseEfsJnDAO.persistir(mcoAnamneseEfsJn);		
	}

	private void inserir(McoAnamneseEfs mcoAnamneseEfs, Integer conNumero, Integer pacCodigo, Short seqp) {
		
		McoAnamneseEfsId pk = new McoAnamneseEfsId();
		pk.setConNumero(conNumero);
		pk.setGsoPacCodigo(pacCodigo);
		pk.setGsoSeqp(seqp);
		
		mcoAnamneseEfs.setId(pk);
		mcoAnamneseEfs.setCriadoEm(new Date());
		mcoAnamneseEfs.setSerVinCodigo(usuario.getVinculo());
		mcoAnamneseEfs.setSerMatricula(usuario.getMatricula());
		
		mcoAnamneseEfs.setConsulta(consultasDAO.obterPorChavePrimaria(conNumero));
		
		anamneseEfsDAO.persistir(mcoAnamneseEfs);
	}

	public void validarBCF(McoAnamneseEfs anamneseEfs,
			boolean permNrBfc, int tipoBCF) throws ApplicationBusinessException {
		
		switch (tipoBCF) {
		case 1:
			validarBCF1(anamneseEfs, permNrBfc);
			break;
		case 2:
			validarBCF2(anamneseEfs, permNrBfc);
			break;
		case 3:
			validarBCF3(anamneseEfs, permNrBfc);
			break;
		case 4:
			validarBCF4(anamneseEfs, permNrBfc);
			break;	
		case 5:
			validarBCF5(anamneseEfs, permNrBfc);
			break;	
		case 6:
			validarBCF6(anamneseEfs, permNrBfc);
			break;
		default:
			break;
		}	
	}

	private void validarBCF6(McoAnamneseEfs anamneseEfs,
			boolean permNrBfc) throws ApplicationBusinessException {

		if(permNrBfc && anamneseEfs.getBatimentoCardiacoFetal6() != null && (anamneseEfs.getBatimentoCardiacoFetal6() < 0 || anamneseEfs.getBatimentoCardiacoFetal() > 200)){
				throw new ApplicationBusinessException(McoAnamneseEfsRNExceptionCode.MENSAGEM_BCF, "BCF6");
		}
	}

	private void validarBCF5(McoAnamneseEfs anamneseEfs,
			boolean permNrBfc) throws ApplicationBusinessException {
		if(permNrBfc && anamneseEfs.getBatimentoCardiacoFetal5() != null && (anamneseEfs.getBatimentoCardiacoFetal5() < 0 || anamneseEfs.getBatimentoCardiacoFetal() > 200)){
			throw new ApplicationBusinessException(McoAnamneseEfsRNExceptionCode.MENSAGEM_BCF, "BCF5");
		}
	}

	private void validarBCF4(McoAnamneseEfs anamneseEfs,
			boolean permNrBfc) throws ApplicationBusinessException {
		if(permNrBfc && anamneseEfs.getBatimentoCardiacoFetal4() != null && (anamneseEfs.getBatimentoCardiacoFetal4() < 0 || anamneseEfs.getBatimentoCardiacoFetal() > 200)){
			throw new ApplicationBusinessException(McoAnamneseEfsRNExceptionCode.MENSAGEM_BCF, "BCF4");
		}
	}

	private void validarBCF3(McoAnamneseEfs anamneseEfs,
			boolean permNrBfc) throws ApplicationBusinessException {
		if(permNrBfc && anamneseEfs.getBatimentoCardiacoFetal3() != null && (anamneseEfs.getBatimentoCardiacoFetal3() < 0 || anamneseEfs.getBatimentoCardiacoFetal() > 200)){
			throw new ApplicationBusinessException(McoAnamneseEfsRNExceptionCode.MENSAGEM_BCF, "BCF3");
		}
	}

	private void validarBCF2(McoAnamneseEfs anamneseEfs,
			boolean permNrBfc) throws ApplicationBusinessException {
		if(permNrBfc && anamneseEfs.getBatimentoCardiacoFetal2() != null && (anamneseEfs.getBatimentoCardiacoFetal2() < 0 || anamneseEfs.getBatimentoCardiacoFetal() > 200)){
			throw new ApplicationBusinessException(McoAnamneseEfsRNExceptionCode.MENSAGEM_BCF, "BCF2");
		}
	}

	private void validarBCF1(McoAnamneseEfs anamneseEfs,
			boolean permNrBfc) throws ApplicationBusinessException {
		if(permNrBfc && anamneseEfs.getBatimentoCardiacoFetal() != null && (anamneseEfs.getBatimentoCardiacoFetal() < 0 || anamneseEfs.getBatimentoCardiacoFetal() > 200)){
			throw new ApplicationBusinessException(McoAnamneseEfsRNExceptionCode.MENSAGEM_BCF, "BCF");
		}
	}
}

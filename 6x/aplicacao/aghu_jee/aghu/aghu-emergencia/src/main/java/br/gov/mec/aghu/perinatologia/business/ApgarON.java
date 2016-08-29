package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoApgarsJn;
import br.gov.mec.aghu.perinatologia.dao.McoApgarsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoApgarsJnDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ApgarON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7873568159804634990L;
	
	private static final Log LOG = LogFactory.getLog(ApgarON.class);

	@Inject @QualificadorUsuario
	private Usuario usuario;

	@Inject
	private McoApgarsDAO mcoApgarsDAO;

	@Inject
	private McoApgarsJnDAO mcoApgarsJnDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ApgarONExceptionCode implements BusinessExceptionCode {
		MCO_00509;
	}
	
	public void persistir(McoApgars apgar) throws BaseException {
		validarSelecaoCombos(apgar);
		calcularMinutosApgar(apgar);

		if(mcoApgarsDAO.obterOriginal(apgar) == null) {
			inserir(apgar);
		} else {
			atualizar(apgar);
		}
	}

	protected void inserir(McoApgars apgar) {
		apgar.setCriadoEm(new Date());
		apgar.setAlteradoEm(new Date());
		apgar.getId().setSerMatricula(usuario.getMatricula());
		apgar.getId().setSerVinCodigo(usuario.getVinculo());
		mcoApgarsDAO.persistir(apgar);
	}
	
	protected void atualizar(McoApgars apgar) {
		apgar.setAlteradoEm(new Date());
		
		processarJn(apgar, DominioOperacoesJournal.UPD);
		
		mcoApgarsDAO.atualizar(apgar);
	}
	
	protected void processarJn(McoApgars apgar, DominioOperacoesJournal operacao) {
		McoApgars original = mcoApgarsDAO.obterOriginal(apgar);
		
		if(modificado(apgar, original)) {
			McoApgarsJn  jn = BaseJournalFactory.getBaseJournal(operacao, McoApgarsJn.class,  usuario.getLogin());
			
			jn.setRnaGsoPacCodigo(original.getId().getRnaGsoPacCodigo());
			jn.setRnaGsoSeqp(original.getId().getRnaGsoSeqp());
			jn.setRnaSeqp(original.getId().getRnaSeqp().shortValue());
			jn.setPacCodigo(original.getPacCodigo());
			jn.setSerMatricula(original.getId().getSerMatricula());
			jn.setSerVinCodigo(original.getId().getSerVinCodigo());
			
			processarJnMinuto1(jn, original);
			processarJnMinuto5(jn, original);
			processarJnMinuto10(jn, original);
			
			mcoApgarsJnDAO.persistir(jn);
		}
	}

	protected Boolean modificado(McoApgars apgar, McoApgars original) {
		if(CoreUtil.modificados(original.getApgar1(), apgar.getApgar1()) ||
			CoreUtil.modificados(original.getFreqCardiaca1(), apgar.getFreqCardiaca1()) ||
			CoreUtil.modificados(original.getEsforcoResp1(), apgar.getEsforcoResp1()) ||
			CoreUtil.modificados(original.getTonoMuscular1(), apgar.getTonoMuscular1()) ||
			CoreUtil.modificados(original.getIrritabilidade1(), apgar.getIrritabilidade1()) ||
			CoreUtil.modificados(original.getCor1(), apgar.getCor1()) ||
			
			CoreUtil.modificados(original.getApgar5(), apgar.getApgar5()) ||
			CoreUtil.modificados(original.getFreqCardiaca5(), apgar.getFreqCardiaca5()) ||
			CoreUtil.modificados(original.getEsforcoResp5(), apgar.getEsforcoResp5()) ||
			CoreUtil.modificados(original.getTonoMuscular5(), apgar.getTonoMuscular5()) ||
			CoreUtil.modificados(original.getIrritabilidade5(), apgar.getIrritabilidade5()) ||
			CoreUtil.modificados(original.getCor5(), apgar.getCor5()) ||
			
			CoreUtil.modificados(original.getApgar10(), apgar.getApgar10()) ||
			CoreUtil.modificados(original.getFreqCardiaca10(), apgar.getFreqCardiaca10()) ||
			CoreUtil.modificados(original.getEsforcoResp10(), apgar.getEsforcoResp10()) ||
			CoreUtil.modificados(original.getTonoMuscular10(), apgar.getTonoMuscular10()) ||
			CoreUtil.modificados(original.getIrritabilidade10(), apgar.getIrritabilidade10()) ||
			CoreUtil.modificados(original.getCor10(), apgar.getCor10())) {
		
			return true;
		}
		
		return false;
	}
	
	protected void processarJnMinuto1(McoApgarsJn  jn, McoApgars original) {
		jn.setApgar1(original.getApgar1() != null ? original.getApgar1().shortValue() : null);
		jn.setFreqCardiaca1(original.getFreqCardiaca1() != null ? original.getFreqCardiaca1().getCodigo() : null);
		jn.setEsforcoResp1(original.getEsforcoResp1() != null ? original.getEsforcoResp1().getCodigo() : null);
		processarJnMinuto1Parcial(jn, original);
	}

	protected void processarJnMinuto1Parcial(McoApgarsJn  jn, McoApgars original) {
		jn.setTonoMuscular1(original.getTonoMuscular1() != null ? original.getTonoMuscular1().getCodigo() : null);
		jn.setIrritabilidade1(original.getIrritabilidade1() != null ? original.getIrritabilidade1().getCodigo() : null);
		jn.setCor1(original.getCor1() != null ? original.getCor1().getCodigo() : null);
	}

	protected void processarJnMinuto5(McoApgarsJn  jn, McoApgars original) {
		jn.setApgar5(original.getApgar5() != null ? original.getApgar5().shortValue() : null);
		jn.setFreqCardiaca5(original.getFreqCardiaca5() != null ? original.getFreqCardiaca5().getCodigo() : null);
		jn.setEsforcoResp5(original.getEsforcoResp5() != null ? original.getEsforcoResp5().getCodigo() : null);
		processarJnMinuto5Parcial(jn, original);
	}

	protected void processarJnMinuto5Parcial(McoApgarsJn  jn, McoApgars original) {
		jn.setTonoMuscular5(original.getTonoMuscular5() != null ? original.getTonoMuscular5().getCodigo() : null);
		jn.setIrritabilidade5(original.getIrritabilidade5() != null ? original.getIrritabilidade5().getCodigo() : null);
		jn.setCor5(original.getCor5() != null ? original.getCor5().getCodigo() : null);
	}

	protected void processarJnMinuto10(McoApgarsJn  jn, McoApgars original) {
		jn.setApgar10(original.getApgar10() != null ? original.getApgar10().shortValue() : null);
		jn.setFreqCardiaca10(original.getFreqCardiaca10() != null ? original.getFreqCardiaca10().getCodigo() : null);
		jn.setEsforcoResp10(original.getEsforcoResp10() != null ? original.getEsforcoResp10().getCodigo() : null);
		processarJnMinuto10Parcial(jn, original);
	}

	protected void processarJnMinuto10Parcial(McoApgarsJn  jn, McoApgars original) {
		jn.setTonoMuscular10(original.getTonoMuscular10() != null ? original.getTonoMuscular10().getCodigo() : null);
		jn.setIrritabilidade10(original.getIrritabilidade10() != null ? original.getIrritabilidade10().getCodigo() : null);
		jn.setCor10(original.getCor10() != null ? original.getCor10().getCodigo() : null);
	}

	protected void validarSelecaoCombos(McoApgars apgar) throws BaseException {
		if(!(apgar.getFreqCardiaca1() != null && apgar.getEsforcoResp1() != null && apgar.getTonoMuscular1() != null 
				&& apgar.getIrritabilidade1() != null && apgar.getCor1() != null) &&
			!(apgar.getFreqCardiaca5() != null && apgar.getEsforcoResp5() != null && apgar.getTonoMuscular5() != null 
				&& apgar.getIrritabilidade5() != null && apgar.getCor5() != null) &&
			!(apgar.getFreqCardiaca10() != null && apgar.getEsforcoResp10() != null && apgar.getTonoMuscular10() != null 
					&& apgar.getIrritabilidade10() != null && apgar.getCor10() != null)) {
			throw new ApplicationBusinessException(ApgarONExceptionCode.MCO_00509);
		}
	}
	
	protected void calcularMinutosApgar(McoApgars apgar) {
		if(apgar.getFreqCardiaca1() != null && apgar.getEsforcoResp1() != null && apgar.getTonoMuscular1() != null 
				&& apgar.getIrritabilidade1() != null && apgar.getCor1() != null) {
			Integer valor = Integer.valueOf(apgar.getFreqCardiaca1().getCodigo()) + Integer.valueOf(apgar.getEsforcoResp1().getCodigo()) 
					+ Integer.valueOf(apgar.getTonoMuscular1().getCodigo()) + Integer.valueOf(apgar.getIrritabilidade1().getCodigo())
					+ Integer.valueOf(apgar.getCor1().getCodigo());
			apgar.setApgar1(valor.byteValue());
		} else {
			apgar.setApgar1(null);
		}
		if(apgar.getFreqCardiaca5() != null && apgar.getEsforcoResp5() != null && apgar.getTonoMuscular5() != null 
				&& apgar.getIrritabilidade5() != null && apgar.getCor5() != null) {
			Integer valor = Integer.valueOf(apgar.getFreqCardiaca5().getCodigo()) + Integer.valueOf(apgar.getEsforcoResp5().getCodigo()) 
					+ Integer.valueOf(apgar.getTonoMuscular5().getCodigo()) + Integer.valueOf(apgar.getIrritabilidade5().getCodigo())
					+ Integer.valueOf(apgar.getCor5().getCodigo());
			apgar.setApgar5(valor.byteValue());
		} else {
			apgar.setApgar5(null);
		}
		if(apgar.getFreqCardiaca10() != null && apgar.getEsforcoResp10() != null && apgar.getTonoMuscular10() != null 
				&& apgar.getIrritabilidade10() != null && apgar.getCor10() != null) {
			Integer valor = Integer.valueOf(apgar.getFreqCardiaca10().getCodigo()) + Integer.valueOf(apgar.getEsforcoResp10().getCodigo()) 
					+ Integer.valueOf(apgar.getTonoMuscular10().getCodigo()) + Integer.valueOf(apgar.getIrritabilidade10().getCodigo())
					+ Integer.valueOf(apgar.getCor10().getCodigo());
			apgar.setApgar10(valor.byteValue());
		} else {
			apgar.setApgar10(null);
		}
	}
}

	package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioApgar5;
import br.gov.mec.aghu.dominio.DominioPesoNascer;
import br.gov.mec.aghu.dominio.DominioPig;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoSnappeJn;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSnappeJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSnappesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTabAdequacaoPesoDAO;
import br.gov.mec.aghu.perinatologia.vo.SnappeElaboradorVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class SnappeRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7873568159804634990L;
	
	private static final Log LOG = LogFactory.getLog(SnappeRN.class);

	@Inject 
	@QualificadorUsuario
	private Usuario usuario;

	@Inject
	private McoTabAdequacaoPesoDAO mcoTabAdequacaoPesoDAO;
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@Inject
	private IPacienteService pacienteService;
	
	@Inject
	private McoSnappesDAO mcoSnappesDAO;
	
	@Inject
	private McoSnappeJnDAO mcoSnappeJnDAO;


	@Override
	protected Log getLogger() {
		return LOG;
	}
	

	public enum SnappeRNExceptionCode implements BusinessExceptionCode {
		INFORME_PRESSAO_MEDIA, INFORME_RAZAO_PO2_FIO2, INFORME_PH_SANGUE, INFORME_CONVULSOES_MULTIPLAS, INFORME_PESO_AO_NASCER, INFORME_VOLUME_URINARIO, INFORME_PIG, INFORME_APGAR5, INFORME_TEMPERATURA, MENSAGEM_SERVICO_INDISPONIVEL,
		MCO_00139, MCO_00645;
	}
	
	
	/**
	 * #27490  - RN02
	 * @param snappe
	 * @throws ApplicationBusinessException
	 */
	public void gravar(McoSnappes snappe, boolean novo) throws ApplicationBusinessException{
		if (novo) {
			inserir(snappe);
		}else {
			atualizar(snappe);	
		}
	}
	
	/**
	 * #27490  - RN -01
	 * @param pacCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public McoSnappes obterSnappePrePreenchido(Integer pacCodigo) throws ApplicationBusinessException{
		McoSnappes snappe = new McoSnappes();
		BigDecimal peso = obterPesoPacientesPorCodigoPaciente(pacCodigo);
		if (peso != null) {
			if (peso.intValue() > 999) {
				snappe.setPesoAoNascer(DominioPesoNascer.PESO_MAIOR);
			}else if (peso.intValue() >= 750 && peso.intValue() <= 999) {
				snappe.setPesoAoNascer(DominioPesoNascer.PESO_MEDIO);
			}else{
				snappe.setPesoAoNascer(DominioPesoNascer.PESO_MENOR);
			}
		}
		Byte apgar5 = mcoRecemNascidosDAO.obterApgar5RecemNascido(pacCodigo);
		if (apgar5 != null) {
			if (apgar5 >=7) {
				snappe.setApgar5(DominioApgar5.MAIOR_SETE);
			}else{
				snappe.setApgar5(DominioApgar5.MENOR_SETE);
			}
		}
		Byte idadeGestacional = mcoRecemNascidosDAO.obterIdadeGestacionalFinalRecemNascido(pacCodigo);
		if (idadeGestacional != null ) {
			Short percentil3 = mcoTabAdequacaoPesoDAO.obterPercentil3(idadeGestacional.shortValue());
			if (percentil3 != null) {	
				if (idadeGestacional.shortValue() < percentil3) {
					snappe.setPig(DominioPig.NAO);
				}else {
					snappe.setPig(DominioPig.SIM);
				}
			}
		}
		return snappe;
	}
	
	/**
	 * RN04
	 * @param snappe
	 * @throws ApplicationBusinessException 
	 */
	private void inserir(McoSnappes snappe) throws ApplicationBusinessException{
		validar(snappe);
		antesInserir(snappe);
		McoSnappes mcoSnappe = mcoSnappesDAO.obterMcoSnappePorId(snappe.getId().getPacCodigo(), snappe.getId().getSeqp());
		if (mcoSnappe != null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.MCO_00139);
		}
		mcoSnappesDAO.persistir(snappe);
	}
	
	/**
	 * #27490  - Atualiza um snappe
	 * @param snappe
	 * @throws ApplicationBusinessException
	 */
	private void atualizar(McoSnappes snappe) throws ApplicationBusinessException{
		McoSnappes snappeOriginal = mcoSnappesDAO.obterOriginal(snappe.getId());
		verificarUsuario(snappeOriginal);
		if (verificarAtualizacoes(snappe, snappeOriginal)) {
			inserirJournal(snappeOriginal, DominioOperacoesJournal.UPD);
		}
		mcoSnappesDAO.merge(snappe);
		
	}
	/**
	 * #27490  - exclui um snappe
	 * @param snappe
	 * @throws ApplicationBusinessException
	 */
	public void excluir(McoSnappes snappe) throws ApplicationBusinessException{
		McoSnappes snappeOriginal = mcoSnappesDAO.obterOriginal(snappe.getId());
		verificarUsuario(snappeOriginal);
		inserirJournal(snappeOriginal, DominioOperacoesJournal.DEL);
		mcoSnappesDAO.removerPorId(snappe.getId());
	
	}
	
	/**
	 * #27490 - Insere na journal
	 * @param snappeOriginal
	 * @param operacao
	 */
	private void inserirJournal(McoSnappes snappeOriginal, DominioOperacoesJournal operacao) {
		McoSnappeJn jn = BaseJournalFactory.getBaseJournal(operacao, McoSnappeJn.class,  usuario.getLogin());
		jn.setSeqp(snappeOriginal.getId().getSeqp());
		jn.setPressaoArtMedia((short) snappeOriginal.getPressaoArtMedia().getCodigo());
		jn.setApgar5((short)  snappeOriginal.getApgar5().getCodigo());
		jn.setConvulsoesMultiplas((short) snappeOriginal.getConvulsoesMultiplas().getCodigo());
		jn.setPesoAoNascer((short) snappeOriginal.getPesoAoNascer().getCodigo());
		jn.setPhDoSangue((short) snappeOriginal.getPhDoSangue().getCodigo());
		jn.setPig((short) snappeOriginal.getPig().getCodigo());
		jn.setRazaoPo2PorFio2((short) snappeOriginal.getRazaoPo2PorFio2().getCodigo());
		jn.setTemperatura((short) snappeOriginal.getTemperatura().getCodigo());
		jn.setVolUrinario((short) snappeOriginal.getVolUrinario().getCodigo());
		jn.setEscoreSnappeii(snappeOriginal.getEscoreSnappeii());
		jn.setCriadoEm(snappeOriginal.getCriadoEm());
		jn.setPacCodigo(snappeOriginal.getId().getPacCodigo());
		jn.setSerVinCodigo(snappeOriginal.getSerVinCodigo());
		jn.setSerMatricula(snappeOriginal.getSerMatricula());
		jn.setDthrSumarioAlta(snappeOriginal.getDthrSumarioAlta());
		mcoSnappeJnDAO.persistir(jn);
	}
	
	
	/**
	 * 
	 * @param snappe
	 * @param snappeOriginal
	 * @return
	 */
	public boolean verificarAtualizacoes(McoSnappes snappe, McoSnappes snappeOriginal){
		if (CoreUtil.modificados(snappeOriginal.getPressaoArtMedia(), snappe.getPressaoArtMedia())
				||CoreUtil.modificados(snappeOriginal.getApgar5(),snappe.getApgar5()) 
				||CoreUtil.modificados(snappeOriginal.getConvulsoesMultiplas(),snappe.getConvulsoesMultiplas())
				||CoreUtil.modificados(snappeOriginal.getPesoAoNascer(), snappe.getPesoAoNascer())
				||CoreUtil.modificados(snappeOriginal.getPhDoSangue(), snappe.getPhDoSangue()) 
				||CoreUtil.modificados(snappeOriginal.getPig(), snappe.getPig()) 
				||CoreUtil.modificados(snappeOriginal.getRazaoPo2PorFio2(), snappe.getRazaoPo2PorFio2())
				||CoreUtil.modificados(snappeOriginal.getTemperatura(), snappe.getTemperatura()) 
				||CoreUtil.modificados(snappeOriginal.getVolUrinario(), snappe.getVolUrinario()) 
				||CoreUtil.modificados(snappeOriginal.getEscoreSnappeii(), snappe.getEscoreSnappeii())){
			return true;
		}
		return false;
	}
	
	
	/**
	 * #27490  - Calcula snappe
	 * @param snappe
	 * @return
	 */
	public Integer calcular(McoSnappes snappe){
		Integer resultado = 0; 
		if (snappe != null) {
			if (snappe.getPressaoArtMedia() != null) {
				resultado += Integer.valueOf(snappe.getPressaoArtMedia().getCodigo());
			}
			if (snappe.getRazaoPo2PorFio2() != null) {
				resultado += Integer.valueOf(snappe.getRazaoPo2PorFio2().getCodigo());
			}
			if (snappe.getConvulsoesMultiplas() != null) {
				resultado += Integer.valueOf(snappe.getConvulsoesMultiplas().getCodigo());
			}
			if (snappe.getTemperatura() != null) {
				resultado += Integer.valueOf(snappe.getTemperatura().getCodigo());
			}
			if (snappe.getPesoAoNascer() != null) {
				resultado += Integer.valueOf(snappe.getPesoAoNascer().getCodigo());
			}
			if (snappe.getPig() != null) {
				resultado += Integer.valueOf(snappe.getPig().getCodigo());
			}
			if (snappe.getVolUrinario() != null) {
				resultado += Integer.valueOf(snappe.getVolUrinario().getCodigo());
			}
			if (snappe.getPhDoSangue() != null) {
				resultado += Integer.valueOf(snappe.getPhDoSangue().getCodigo());
			}
			if (snappe.getApgar5() != null) {
				resultado += Integer.valueOf(snappe.getApgar5().getCodigo());
			}
		}
		return resultado;
	}
	
	/**
	 * @ORADB - mcok_sna_rn.rn_snap_ver_user
	 * @param snappeOriginal
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarUsuario(McoSnappes snappeOriginal) throws ApplicationBusinessException{
		if (!verificarUsuarioAlteracao(snappeOriginal)) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.MCO_00645);
		} 
	}
	
	public boolean verificarUsuarioAlteracao(McoSnappes snappe) {
		return snappe.getSerMatricula().equals(usuario.getMatricula()) && snappe.getSerVinCodigo().equals(usuario.getVinculo());
	}
	
	/**
	 * #27490 - Verifica se existe registro do usuario na listagem
	 * @param lista
	 * @return
	 */
	public boolean existeRegistroUsuario(List<SnappeElaboradorVO> lista){
		for (SnappeElaboradorVO snappeElaboradorVO : lista) {
			if (usuario.getVinculo().equals(snappeElaboradorVO.getSerVinCodigo()) && usuario.getMatricula().equals(snappeElaboradorVO.getSerMatricula())) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * #27490 
	 * @ORADB - Trigger Before Insert – AGH.MCO_SNAPPES.MCOT_SNA_BRI
	 * @param snappe
	 */
	private McoSnappes antesInserir(McoSnappes snappe){
		snappe.setSerMatricula(usuario.getMatricula());
		snappe.setSerVinCodigo(usuario.getVinculo());
		snappe.setCriadoEm(new Date());
		return snappe;
	}
	
	/**
	 * #27490 -Valida campos obrigatorios
	 * @param snappe
	 * @throws ApplicationBusinessException
	 */
	private void validar(McoSnappes snappe) throws ApplicationBusinessException{
		if (snappe.getPressaoArtMedia() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_PRESSAO_MEDIA);
		}
		if (snappe.getRazaoPo2PorFio2() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_RAZAO_PO2_FIO2);
		}
		if (snappe.getPhDoSangue() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_PH_SANGUE);
		}
		if (snappe.getConvulsoesMultiplas() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_CONVULSOES_MULTIPLAS);
		}
		if (snappe.getPesoAoNascer() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_PESO_AO_NASCER);
		}
		if (snappe.getVolUrinario() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_VOLUME_URINARIO);
		}
		if (snappe.getPig() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_PIG);
		}
		if (snappe.getApgar5() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_APGAR5);
		}
		if (snappe.getTemperatura() == null) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.INFORME_TEMPERATURA);
		}
	}
	
	private BigDecimal obterPesoPacientesPorCodigoPaciente(Integer pacCodigo)throws ApplicationBusinessException {
		BigDecimal peso;
		try {
			peso = pacienteService.obterPesoPacientesPorCodigoPaciente(pacCodigo);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(SnappeRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return peso;
	}

	public boolean existeAlteracaoSnappe(McoSnappes newSnappes) {
		McoSnappes oldSnappe = mcoSnappesDAO.obterOriginal(newSnappes);
		
		if (oldSnappe != null) {
			if(CoreUtil.modificados(newSnappes.getCriadoEm(), oldSnappe.getCriadoEm())
					|| CoreUtil.modificados(newSnappes.getDthrSumarioAlta(), oldSnappe.getDthrSumarioAlta())
					|| CoreUtil.modificados(newSnappes.getApgar5(), oldSnappe.getApgar5())
					|| CoreUtil.modificados(newSnappes.getApgar5Codigo(), oldSnappe.getApgar5Codigo())
					|| CoreUtil.modificados(newSnappes.getConvulsoesMultiplas(), oldSnappe.getConvulsoesMultiplas())
					|| CoreUtil.modificados(newSnappes.getConvulsoesMultiplasCodigo(), oldSnappe.getConvulsoesMultiplasCodigo())
					|| CoreUtil.modificados(newSnappes.getEscoreSnappeii(), oldSnappe.getEscoreSnappeii())
					|| CoreUtil.modificados(newSnappes.getId(), oldSnappe.getId())
					|| CoreUtil.modificados(newSnappes.getPesoAoNascer(), oldSnappe.getPesoAoNascer())
					|| CoreUtil.modificados(newSnappes.getPesoAoNascerCodigo(), oldSnappe.getPesoAoNascerCodigo())
					|| CoreUtil.modificados(newSnappes.getPhDoSangue(), oldSnappe.getPhDoSangue())
					|| CoreUtil.modificados(newSnappes.getPhDoSangueCodigo(), oldSnappe.getPhDoSangueCodigo())
					|| CoreUtil.modificados(newSnappes.getPig(), oldSnappe.getPig())
					|| CoreUtil.modificados(newSnappes.getPigCodigo(), oldSnappe.getPigCodigo())
					|| CoreUtil.modificados(newSnappes.getPressaoArtMedia(), oldSnappe.getPressaoArtMedia())
					|| CoreUtil.modificados(newSnappes.getPressaoArtMediaCodigo(), oldSnappe.getPressaoArtMediaCodigo())
					|| CoreUtil.modificados(newSnappes.getRazaoPo2PorFio2(), oldSnappe.getRazaoPo2PorFio2())
					|| CoreUtil.modificados(newSnappes.getRazaoPo2PorFio2Codigo(), oldSnappe.getRazaoPo2PorFio2Codigo())
					|| CoreUtil.modificados(newSnappes.getSerMatricula(), oldSnappe.getSerMatricula())
					|| CoreUtil.modificados(newSnappes.getSerVinCodigo(), oldSnappe.getSerVinCodigo())
					|| CoreUtil.modificados(newSnappes.getTemperatura(), oldSnappe.getTemperatura())
					|| CoreUtil.modificados(newSnappes.getTemperaturaCodigo(), oldSnappe.getTemperaturaCodigo())
					|| CoreUtil.modificados(newSnappes.getVolUrinario(), oldSnappe.getVolUrinario())
					|| CoreUtil.modificados(newSnappes.getVolUrinarioCodigo(), oldSnappe.getVolUrinarioCodigo())){
				return true;
			}
		}
		return false;
	}
}

package br.gov.mec.aghu.procedimentoterapeutico.business;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptParamCalculoDoses;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmDosagemDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptParamCalculoDosesDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloItemMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptVersaoProtocoloSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametroDoseUnidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MptProtocoloMedicamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptProtocoloMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	@Inject
	private MptProtocoloMedicamentosDAO mptProtocoloMedicamentosDAO;
	@Inject
	private MptProtocoloItemMedicamentosDAO mptProtocoloItemMedicamentosDAO;
	@Inject
	private MptParamCalculoDosesDAO mptParamCalculoDosesDAO;
	@Inject
	private MptVersaoProtocoloSessaoDAO mptVersaoProtocoloSessaoDAO;
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO  mpmTipoFrequenciaAprazamentoDAO;
	@Inject 
	private VMpmDosagemDAO vMpmDosagemDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
		
	private static final long serialVersionUID = 2797194818319163103L;
	
	private BigDecimal defaultMaximo = new BigDecimal("999.000");
	private BigDecimal defaultMinimo = new BigDecimal("0.000");
	
	@Inject
	private CadastroSolucaoProtocoloRN cadastroSolucaoProtocoloRN;
	

	public void inserirMedicamento(MptProtocoloMedicamentos mptProtocoloMedicamentos, MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, Integer vpsSeq, ProtocoloMedicamentoSolucaoCuidadoVO parametroSelecionado, List<ParametroDoseUnidadeVO> listaParam, List<ParametroDoseUnidadeVO> listaParamExcluidos) throws ApplicationBusinessException{
		
		MptVersaoProtocoloSessao versaoProtocoloSessao = mptVersaoProtocoloSessaoDAO.obterPorChavePrimaria(vpsSeq);
		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(mptProtocoloMedicamentos.getTfqSeq().getSeq());
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
	
		if(listaParamExcluidos != null && !listaParamExcluidos.isEmpty()){
			for (ParametroDoseUnidadeVO parametroDoseUnidadeVO : listaParamExcluidos) {
				MptParamCalculoDoses objRemover = mptParamCalculoDosesDAO.obterPorChavePrimaria(parametroDoseUnidadeVO.getSeq());
				mptParamCalculoDosesDAO.remover(objRemover);
			}
		}
		
		if(parametroSelecionado != null){
			this.mptProtocoloMedicamentosDAO.atualizar(mptProtocoloMedicamentos);
			this.mptProtocoloItemMedicamentosDAO.atualizar(mptProtocoloItemMedicamentos);
			preencherParametroDoseParaPersistencia(mptProtocoloItemMedicamentos, listaParam, servidorLogado);
		}else{
			mptProtocoloMedicamentos.setServidor(servidorLogado);
			mptProtocoloMedicamentos.setTfqSeq(tipoFrequenciaAprazamento);
			mptProtocoloMedicamentos.setVersaoProtocoloSessao(versaoProtocoloSessao);
			mptProtocoloMedicamentos.setIndSolucao(false);
			mptProtocoloMedicamentos.setIndNaoPermiteAlteracao(false);
			this.mptProtocoloMedicamentosDAO.persistir(mptProtocoloMedicamentos);
			
			mptProtocoloItemMedicamentos.setProtocoloMedicamentos(mptProtocoloMedicamentos);
			mptProtocoloItemMedicamentos.setServidor(servidorLogado);
			mptProtocoloItemMedicamentos.setCriadoEm(new Date());
			
			this.mptProtocoloItemMedicamentosDAO.persistir(mptProtocoloItemMedicamentos);
			
			preencherParametroDoseParaPersistencia(mptProtocoloItemMedicamentos, listaParam, servidorLogado);
		}
	}

	private void preencherParametroDoseParaPersistencia(MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, List<ParametroDoseUnidadeVO> listaParam, RapServidores servidorLogado) {
		for(ParametroDoseUnidadeVO item : listaParam){
			if (item.getSeq() > 0) {
				MptParamCalculoDoses itemOriginal = mptParamCalculoDosesDAO.obterOriginal(item.getSeq());
				atualizarParametroCalculo(mptProtocoloItemMedicamentos, item, itemOriginal);
			} else {
				MptParamCalculoDoses parametroDose = new MptParamCalculoDoses();
				montarObjeto(mptProtocoloItemMedicamentos, item, parametroDose);
				parametroDose.setCriadoEm(new Date());
				parametroDose.setServidor(servidorLogado);
				mptParamCalculoDosesDAO.persistir(parametroDose);
			}
			
		}
	}

	private void montarObjeto(MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, ParametroDoseUnidadeVO item, MptParamCalculoDoses parametroDose) {
		if(item.getAfaFormaDosagemVO() != null && item.getAfaFormaDosagemVO().getFdsSeq() != null){
			parametroDose.setAfaFormaDosagem(afaFormaDosagemDAO.obterPorChavePrimaria(item.getAfaFormaDosagemVO().getFdsSeq()));
		}
		parametroDose.setAlertaCalculoDose(item.getAlertaCalculoDose());
		parametroDose.setDose(item.getDose());
		parametroDose.setDoseMaximaUnitaria(item.getDoseMaximaUnitaria());
		parametroDose.setIdadeMaxima(item.getIdadeMaxima());
		parametroDose.setIdadeMinima(item.getIdadeMinima());
		parametroDose.setMptProtocoloItemMedicamentos(mptProtocoloItemMedicamentos);
		parametroDose.setPesoMaximo(item.getPesoMaximo());
		parametroDose.setPesoMinimo(item.getPesoMinimo());
		parametroDose.setPmiSeq(item.getPmiSeq());
		parametroDose.setTipoCalculo(item.getTipoCalculo());
		parametroDose.setUnidBaseCalculo(item.getUnidBaseCalculo());
		if(item.getIdadeMaxima() != null || item.getIdadeMinima() != null){
			parametroDose.setUnidIdade(item.getUnidIdade());
		}
		cadastroSolucaoProtocoloRN.preInserirParametroDoseMedicamentoSolucao(parametroDose);
	}

	private void atualizarParametroCalculo(MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, ParametroDoseUnidadeVO item, MptParamCalculoDoses paramDose) {
		montarObjeto(mptProtocoloItemMedicamentos, item, paramDose);
		mptParamCalculoDosesDAO.merge(paramDose);
	}

	public void alterarSolucao(MptProtocoloMedicamentos solucao,
			List<ProtocoloItensMedicamentoVO> listaMedicamentos, 
			List<ProtocoloItensMedicamentoVO> listaMedicamentosExclusao) throws ApplicationBusinessException {
		
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		if (listaMedicamentosExclusao != null && !listaMedicamentosExclusao.isEmpty()) {
			for (ProtocoloItensMedicamentoVO itemMedVO : listaMedicamentosExclusao) {
				List<MptParamCalculoDoses> itemParamDose = mptParamCalculoDosesDAO.obterListaDoseMdtosPorSeqMdto(itemMedVO.getPimSeq());
				for (MptParamCalculoDoses item : itemParamDose) {
					mptParamCalculoDosesDAO.remover(item);
				}
				MptProtocoloItemMedicamentos itemMed = mptProtocoloItemMedicamentosDAO.obterPorChavePrimaria(itemMedVO.getPimSeq());
				if(itemMed != null){
					mptProtocoloItemMedicamentosDAO.remover(itemMed);
				}
			}
		}
		
		for (ProtocoloItensMedicamentoVO itemMedVO : listaMedicamentos) {
			VMpmDosagem dosagem = new VMpmDosagem();
			if(itemMedVO.getDosagem() == null){
				dosagem = vMpmDosagemDAO.buscarMpmDosagemPorSeqDosagem(itemMedVO.getFdsSeq());
			} else {
				dosagem = itemMedVO.getDosagem();
			}
			if (itemMedVO.getPimSeq() < 0) {
				MptProtocoloItemMedicamentos itemMedicamentos = new MptProtocoloItemMedicamentos();
				// incluir
				preencherItemMed(solucao, servidorLogado, itemMedVO, itemMedicamentos, dosagem, true);
				mptProtocoloItemMedicamentosDAO.persistir(itemMedicamentos);
				// incluir filhos
				for(ParametroDoseUnidadeVO paramDoseUniVO : itemMedVO.getListaParametroCalculo()){
					persistirDose(servidorLogado, itemMedicamentos,	paramDoseUniVO);
				}
				
			} else {
				// alterar
				MptProtocoloItemMedicamentos itemMedicamentosOriginal = mptProtocoloItemMedicamentosDAO.obterPorChavePrimaria(itemMedVO.getPimSeq());
				preencherItemMed(solucao, servidorLogado, itemMedVO, itemMedicamentosOriginal, dosagem, false);
				mptProtocoloItemMedicamentosDAO.merge(itemMedicamentosOriginal);
				// verificar filhos
				for (ParametroDoseUnidadeVO paramVO : itemMedVO.getListaParametroCalculo()) {
					// pegar seq do pai
					if (paramVO.getSeq() < 0) {
						persistirDose(servidorLogado, itemMedicamentosOriginal,	paramVO);
					} else {
						// alterar filho
						MptParamCalculoDoses mptParamCalcOriginal = mptParamCalculoDosesDAO.obterPorChavePrimaria(paramVO.getSeq());
						if(mptParamCalcOriginal == null){
							persistirDose(servidorLogado, itemMedicamentosOriginal,	paramVO);
						} else {
							preencherParamCalcDose(servidorLogado, itemMedicamentosOriginal, paramVO, mptParamCalcOriginal, false);
							preInserirParametroDoseMedicamentoSolucao(mptParamCalcOriginal);
							mptParamCalculoDosesDAO.merge(mptParamCalcOriginal);
						}
					}
				}
			} 
		}
		
		mptProtocoloMedicamentosDAO.atualizar(solucao);
		
	}

	private void persistirDose(RapServidores servidorLogado,
			MptProtocoloItemMedicamentos itemMedicamentos,
			ParametroDoseUnidadeVO paramDoseUniVO) {
		MptParamCalculoDoses mptParamCalcDose = new MptParamCalculoDoses();
		preencherParamCalcDose(servidorLogado, itemMedicamentos, paramDoseUniVO, mptParamCalcDose, true);
		preInserirParametroDoseMedicamentoSolucao(mptParamCalcDose);
		mptParamCalculoDosesDAO.persistir(mptParamCalcDose);
	}

	private void preencherParamCalcDose(RapServidores servidorLogado,
			MptProtocoloItemMedicamentos itemMedicamentos,
			ParametroDoseUnidadeVO paramDoseUniVO,
			MptParamCalculoDoses mptParamCalcDose, Boolean isPersistir) {
		if(isPersistir){
			mptParamCalcDose.setCriadoEm(new Date());
			mptParamCalcDose.setServidor(servidorLogado);
		}
		if(paramDoseUniVO.getComboUnidade() != null && paramDoseUniVO.getComboUnidade().getFormaDosagem() != null){
			mptParamCalcDose.setAfaFormaDosagem(paramDoseUniVO.getComboUnidade().getFormaDosagem());
		}
		mptParamCalcDose.setAlertaCalculoDose(paramDoseUniVO.getAlertaCalculoDose());
		mptParamCalcDose.setDose(paramDoseUniVO.getDose());
		mptParamCalcDose.setDoseMaximaUnitaria(paramDoseUniVO.getDoseMaximaUnitaria());
		mptParamCalcDose.setIdadeMaxima(paramDoseUniVO.getIdadeMaxima());
		mptParamCalcDose.setIdadeMinima(paramDoseUniVO.getIdadeMinima());
		mptParamCalcDose.setMptProtocoloItemMedicamentos(itemMedicamentos);
		mptParamCalcDose.setPesoMaximo(paramDoseUniVO.getPesoMaximo());
		mptParamCalcDose.setPesoMinimo(paramDoseUniVO.getPesoMinimo());
		mptParamCalcDose.setTipoCalculo(paramDoseUniVO.getTipoCalculo());
		mptParamCalcDose.setUnidBaseCalculo(paramDoseUniVO.getUnidBaseCalculo());
		mptParamCalcDose.setUnidIdade(paramDoseUniVO.getUnidIdade());
	}

	private void preencherItemMed(MptProtocoloMedicamentos solucao,
			RapServidores servidorLogado,
			ProtocoloItensMedicamentoVO itemMedVO,
			MptProtocoloItemMedicamentos itemMedicamentos, VMpmDosagem dosagem,
			Boolean isPersistir) {
		if(isPersistir){
			itemMedicamentos.setServidor(servidorLogado);
			itemMedicamentos.setCriadoEm(new Date());
		}
		itemMedicamentos.setAfaFormaDosagem(dosagem.getFormaDosagem());
		itemMedicamentos.setDose(itemMedVO.getPimDose());
		itemMedicamentos.setMedMatCodigo(itemMedVO.getMedMatCodigo());
		itemMedicamentos.setObservacao(itemMedVO.getPimObservacao());
		itemMedicamentos.setProtocoloMedicamentos(solucao);
	}
	
	public void preInserirParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose) {
		if (calculoDose.getIdadeMaxima() == null){
			calculoDose.setIdadeMaxima(Short.valueOf("999"));
		}
		if(calculoDose.getIdadeMinima() == null){
			calculoDose.setIdadeMinima(Short.valueOf("0"));
		}
		if(calculoDose.getPesoMaximo() == null){
			calculoDose.setPesoMaximo(defaultMaximo);
		}
		if(calculoDose.getPesoMinimo() == null){
			calculoDose.setPesoMinimo(defaultMinimo);
		}
	}
	
	public BigDecimal getDefaultMaximo() {
		return defaultMaximo;
	}

	public void setDefaultMaximo(BigDecimal defaultMaximo) {
		this.defaultMaximo = defaultMaximo;
	}

	public BigDecimal getDefaultMinimo() {
		return defaultMinimo;
	}

	public void setDefaultMinimo(BigDecimal defaultMinimo) {
		this.defaultMinimo = defaultMinimo;
	}
}

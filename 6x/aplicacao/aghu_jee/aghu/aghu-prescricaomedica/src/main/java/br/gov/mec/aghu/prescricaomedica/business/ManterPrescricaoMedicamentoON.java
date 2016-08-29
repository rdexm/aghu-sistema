package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.action.ManterPrescricaoMedicamentoExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicamentoVO;
import br.gov.mec.aghu.view.VMpmDosagem;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ManterPrescricaoMedicamentoON extends BaseBusiness {


	@EJB
	private ItemPrescricaoMedicamentoRN itemPrescricaoMedicamentoRN;
	
	@EJB
	private PrescricaoMedicamentoRN prescricaoMedicamentoRN;
	
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoMedicamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6544423552041001474L;

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public List<VMpmDosagem> buscarDosagensMedicamento(Integer medMatCodigo) {
		return this.getMpmPrescricaoMdtoDAO().buscarDosagensMedicamento(
				medMatCodigo);
	}

	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosPelaChavePrescricao(
			MpmPrescricaoMedicaId prescriao, Date dtHrFimPrescricaoMedica,
			Boolean isSolucao) {
		List<MpmPrescricaoMdto> lista =	this.getMpmPrescricaoMdtoDAO()
				.obterListaMedicamentosPrescritosPelaChavePrescricao(prescriao,
						dtHrFimPrescricaoMedica, isSolucao, false);
		for(MpmPrescricaoMdto prescricaoMdto : lista){
			if(prescricaoMdto.getItensPrescricaoMdtos() != null){
				prescricaoMdto.getItensPrescricaoMdtos().size();
			}
			
			prescricaoMdto.getDescricaoFormatada();
		}
		return lista;
	}

	public Boolean isUnidadeBombaInfusao(AghUnidadesFuncionais unidade) {
		return this.getPacienteFacade().isUnidadeFuncionalBombaInfusao(
				unidade.getSeq());
	}

	public Boolean validaBombaInfusao(AghUnidadesFuncionais unidade,
			AfaViaAdministracao viaAdministracao, AfaMedicamento medicamento) {
		if (!this.isUnidadeBombaInfusao(unidade)
				|| !getFarmaciaFacade().verificarViaAssociadaAoMedicamento(medicamento.getMatCodigo(), viaAdministracao.getSigla())) {
			return false;
		} else {
			return true;
		}
	}
	
	public List<MpmTipoFrequenciaAprazamento> buscarTipoFrequenciaAprazamento(String strPesquisa) {
		return getMpmTipoFrequenciaAprazamentoDAO()
				.obterListaTipoFrequenciaAprazamento(strPesquisa);
	}
	
	public Long buscarTipoFrequenciaAprazamentoCount(String strPesquisa) {
		return getMpmTipoFrequenciaAprazamentoDAO()
				.obterListaTipoFrequenciaAprazamentoCount(strPesquisa);
	}

	private MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}

	private MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	private IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected PrescricaoMedicamentoRN getPrescricaoMedicamentoRN() {
		return prescricaoMedicamentoRN;
	}

	protected ItemPrescricaoMedicamentoRN getItemPrescricaoMedicamentoRN() {
		return itemPrescricaoMedicamentoRN;
	}

	public void excluirPrescricaoMedicamento(MpmPrescricaoMdto prescricaoMedicamento) 
	throws ApplicationBusinessException {
		try {
			
			//########################################################################################################################
			//FIXME MIGRAÇÃO: Aqui alterou o valor do atributo que agora deve ser passado como parâmetro
			//this.getItemPrescricaoMedicamentoRN().setaAtributoDeletarItemPrescricaoMedicamento(Boolean.TRUE);
			//########################################################################################################################
			Boolean autoExcluirProcedimentoMedicamentoSol  = Boolean.TRUE;
			if (prescricaoMedicamento.getItensPrescricaoMdtos() != null) {
				for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
						.getItensPrescricaoMdtos()) {
					getItemPrescricaoMedicamentoRN().excluirItemPrescricaoMedicamento(itemPrescricaoMedicamento, autoExcluirProcedimentoMedicamentoSol);
				}
			}
			
			getPrescricaoMedicamentoRN().excluirPrescricaoMedicamento(
					prescricaoMedicamento);

			getPrescricaoMedicamentoRN().flush();
			
		//########################################################################################################################
			//FIXME MIGRAÇÃO: Mas depois de fazer todo o processo, ele tem que passar a váriavel para FALSE de novo
			//Será que devo retornar o valor desta variável? 
			//this.getItemPrescricaoMedicamentoRN().setaAtributoDeletarItemPrescricaoMedicamento(Boolean.FALSE);
		} catch(ApplicationBusinessException | BaseRuntimeException  e){
			//Mas como fazer isso se tem um throw?
			//this.getItemPrescricaoMedicamentoRN().setaAtributoDeletarItemPrescricaoMedicamento(Boolean.FALSE);
			LOG.error("Erro", e);
			throw e;
		}
		//########################################################################################################################
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void inserirPrescricaoMedicamentoModeloBasico(MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador) throws BaseException {
		//AJUSTE DILUENTE
		if(prescricaoMedicamento.getDiluente() != null) {
			if(prescricaoMedicamento.getItensPrescricaoMdtos().size() == 1) {
				List<VMpmDosagem> dosagens = this.buscarDosagensMedicamento(prescricaoMedicamento.getDiluente().getMatCodigo());
				AfaFormaDosagem	formaDosagem = getFarmaciaFacade().buscarDosagenPadraoMedicamento(prescricaoMedicamento.getDiluente().getMatCodigo());
				VMpmDosagem unidDosg = null;
				if(formaDosagem != null){
					for(int i = 0; i < dosagens.size(); i++){
						if(formaDosagem.getSeq().equals(dosagens.get(i).getFormaDosagem().getSeq())){
							unidDosg = dosagens.get(i);
						}
					}
				}
				else {
					if(dosagens != null && !dosagens.isEmpty()) {
						unidDosg = dosagens.get(0);
						formaDosagem = dosagens.get(0).getFormaDosagem();
					}
				}

				MpmItemPrescricaoMdto itemPrescricaoMedicamento = new MpmItemPrescricaoMdto(); 
				itemPrescricaoMedicamento.setId(new MpmItemPrescricaoMdtoId(prescricaoMedicamento.getId().getAtdSeq(), prescricaoMedicamento.getId().getSeq(), prescricaoMedicamento.getDiluente().getMatCodigo(), Short.valueOf("1")));
				itemPrescricaoMedicamento.setMedicamento(prescricaoMedicamento.getDiluente());
				itemPrescricaoMedicamento.setDose((formaDosagem != null && formaDosagem.getFatorConversaoUp()!=null)?formaDosagem.getFatorConversaoUp():prescricaoMedicamento.getDiluente().getConcentracao());
				itemPrescricaoMedicamento.setFormaDosagem(unidDosg!=null?unidDosg.getFormaDosagem():null);
				itemPrescricaoMedicamento.setMdtoAguardaEntrega(false);
				itemPrescricaoMedicamento.setOrigemJustificativa(false);
				itemPrescricaoMedicamento
				.setQtdeCalcSist24h(getItemPrescricaoMedicamentoRN().buscaCalculoQuantidade24Horas(
								prescricaoMedicamento.getFrequencia(),
								prescricaoMedicamento
								.getTipoFreqAprazamento() != null ? prescricaoMedicamento
										.getTipoFreqAprazamento().getSeq()
										: null,
										prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getDose(),
										prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getFormaDosagem() != null ? prescricaoMedicamento.getItensPrescricaoMdtos().get(0)
												.getFormaDosagem().getSeq()
												: null,
												prescricaoMedicamento.getDiluente() != null ? prescricaoMedicamento.getDiluente()
														.getMatCodigo() : null));
				itemPrescricaoMedicamento.setPrescricaoMedicamento(prescricaoMedicamento);
				prescricaoMedicamento.getItensPrescricaoMdtos().add(itemPrescricaoMedicamento);
			}
		}

		getPrescricaoMedicamentoRN().inserirPrescricaoMedicamentoModeloBasico(prescricaoMedicamento, nomeMicrocomputador);
		ItemPrescricaoMedicamentoRN itemPrescricaoMedicamentoRN = getItemPrescricaoMedicamentoRN();
		if (prescricaoMedicamento.getItensPrescricaoMdtos() != null) {
			for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
					.getItensPrescricaoMdtos()) {
				itemPrescricaoMedicamento.getId().setPmdSeq(prescricaoMedicamento.getId().getSeq().longValue());
				itemPrescricaoMedicamentoRN.inserirItemPrescricaoMedicamentoModeloBasico(itemPrescricaoMedicamento);
			}
		}
	}

	public void persistirPrescricaoMedicamento(
			MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador
			, MpmPrescricaoMdto prescricaoMedicamentoOriginal)
			throws BaseException {
		// Chama o método de persistência considerando que o item não 
		// foi originado de uma cópia quando da criação de uma nova Prescrição
		persistirPrescricaoMedicamento(prescricaoMedicamento, false, nomeMicrocomputador,prescricaoMedicamentoOriginal);
	}
	
	public void persistirPrescricaoMedicamento(
			MpmPrescricaoMdto prescricaoMedicamento, Boolean isCopiado, String nomeMicrocomputador
			, MpmPrescricaoMdto prescricaoMedicamentoOriginal)
			throws BaseException {
			Boolean autoExcluirProcedimentoMedicamentoSol = null;
//			prescricaoMedicamento = reatachar(prescricaoMedicamento);
			
			if (prescricaoMedicamento.getId() == null
					|| prescricaoMedicamento.getId().getSeq() == null) {
				// Inserção
				getPrescricaoMedicamentoRN().inserirPrescricaoMedicamento(
						prescricaoMedicamento, nomeMicrocomputador, isCopiado);
	
				ItemPrescricaoMedicamentoRN itemPrescricaoMedicamentoRN = getItemPrescricaoMedicamentoRN();
	
				if (prescricaoMedicamento.getItensPrescricaoMdtos() != null) {
					for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
							.getItensPrescricaoMdtos()) {
						itemPrescricaoMedicamento = itemPrescricaoMedicamentoRN.reatachar(itemPrescricaoMedicamento);
						MpmItemPrescricaoMdtoId idItem = new MpmItemPrescricaoMdtoId();
						idItem.setPmdAtdSeq(prescricaoMedicamento.getPrescricaoMedica().getId().getAtdSeq());
						idItem.setPmdSeq(prescricaoMedicamento.getId().getSeq());
						idItem.setMedMatCodigo(itemPrescricaoMedicamento
								.getMedicamento().getMatCodigo());
						idItem.setSeqp((short) 1);
						
						itemPrescricaoMedicamento.setId(idItem);
						itemPrescricaoMedicamentoRN
							.inserirItemPrescricaoMedicamento(itemPrescricaoMedicamento, isCopiado);
					}
				}
				getPrescricaoMedicamentoRN().flush();
			} else {
				// Edição
				ItemPrescricaoMedicamentoRN itemPrescricaoMedicamentoRN = getItemPrescricaoMedicamentoRN();

				//OBTER VO ITENS - REMOVER OS QUE NÃO EXISTEM MAIS NA COLLECTION
				List<ItemPrescricaoMedicamentoVO> listaItensVO = getMpmItemPrescricaoMdtoDAO().obterListaItemPrescricaoMdtoVO(prescricaoMedicamento.getPrescricaoMedica().getId().getAtdSeq(), prescricaoMedicamento.getId().getSeq());
				boolean remove = true;
				for ( Iterator<ItemPrescricaoMedicamentoVO> itr = listaItensVO.iterator(); itr.hasNext(); ) {					
					ItemPrescricaoMedicamentoVO vo = itr.next();
					remove = false;
					for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
							.getItensPrescricaoMdtos()) {
							if(itemPrescricaoMedicamento.getId() != null && vo.getMatCodigo().equals(itemPrescricaoMedicamento.getId().getMedMatCodigo())) {
								remove = true;
								break;
							}
					}
					if(remove) {
						itr.remove();
					}
				}

				for(ItemPrescricaoMedicamentoVO vo : listaItensVO) {
					MpmItemPrescricaoMdto item = getMpmItemPrescricaoMdtoDAO().obterPorChavePrimaria(new MpmItemPrescricaoMdtoId(vo.getPmdAtdSeq(), vo.getPmdSeq(), vo.getMatCodigo(), vo.getSeqp()));
					//#############################################################################################################
					//FIXME MIGRAÇÃO: Talvez o valor passado aqui seja sempre FALSE, pois ao passar por parâmetro, altera muitos métodos
					autoExcluirProcedimentoMedicamentoSol = Boolean.FALSE;
					getItemPrescricaoMedicamentoRN().excluirItemPrescricaoMedicamento(item, autoExcluirProcedimentoMedicamentoSol);
					//#############################################################################################################
				}
								
				if (prescricaoMedicamento.getItensPrescricaoMdtos() != null) {
					for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
							.getItensPrescricaoMdtos()) {
						if(itemPrescricaoMedicamento.getId() == null || itemPrescricaoMedicamento.getId().getSeqp() == null) {
							MpmItemPrescricaoMdtoId idItem = new MpmItemPrescricaoMdtoId();
							idItem.setPmdAtdSeq(prescricaoMedicamento.getPrescricaoMedica().getId().getAtdSeq());
							idItem.setPmdSeq(prescricaoMedicamento.getId().getSeq());
							idItem.setMedMatCodigo(itemPrescricaoMedicamento
									.getMedicamento().getMatCodigo());
							idItem.setSeqp((short) 1);
							
							itemPrescricaoMedicamento.setId(idItem);
		
							itemPrescricaoMedicamentoRN
									.inserirItemPrescricaoMedicamento(itemPrescricaoMedicamento);
						} else {
							itemPrescricaoMedicamentoRN
									.atualizarItemPrescricaoMedicamento(itemPrescricaoMedicamento);
						}
					}
				}
				
				getPrescricaoMedicamentoRN().atualizarPrescricaoMedicamento(
						prescricaoMedicamento, nomeMicrocomputador, prescricaoMedicamentoOriginal,autoExcluirProcedimentoMedicamentoSol);

				getPrescricaoMedicamentoRN().flush();
			}
			
	}

	public void verificaGrupoUsoMedicamentoTuberculostatico(List<MpmItemPrescricaoMdto> listItensMedicamentos) throws BaseException {
		for (MpmItemPrescricaoMdto itemPrescricaoMdto : listItensMedicamentos) {
			itemPrescricaoMedicamentoRN.verificaGrupoUsoMedicamentoTuberculostatico(
				itemPrescricaoMdto.getId().getPmdAtdSeq(),
				itemPrescricaoMdto.getId().getMedMatCodigo(),
				itemPrescricaoMdto.getJustificativaUsoMedicamento() != null ? itemPrescricaoMdto
						.getJustificativaUsoMedicamento().getSeq()
						: null);			
		}
	}

	public void verificaDoseFracionada(Integer codigoMedicamento, BigDecimal dose,
			Integer seqFormaDosagem) throws ApplicationBusinessException {
		getItemPrescricaoMedicamentoRN().verificaDoseFracionada(codigoMedicamento, 
				dose, seqFormaDosagem);		
	}	
	
	public MpmPrescricaoMdto ajustaIndPendenteN(MpmPrescricaoMdto prescricaoMedicamento) throws ApplicationBusinessException {
		return getPrescricaoMedicamentoRN().ajustaIndPendente(prescricaoMedicamento);
	}

	public MpmPrescricaoMdto ajustaIndPendenteN(MpmPrescricaoMdto prescricaoMedicamento, List<MpmItemPrescricaoMdto> listaItens) throws ApplicationBusinessException {
		return getPrescricaoMedicamentoRN().ajustaIndPendente(prescricaoMedicamento, listaItens);
	}

	public List<MpmItemPrescricaoMdto> clonarItensPrescricaoMedicamento(MpmPrescricaoMdto prescricaoMedicamento) throws ApplicationBusinessException {
		return getPrescricaoMedicamentoRN().clonarItensPrescricaoMedicamento(prescricaoMedicamento);
	}

	public void persistirPrescricaoMedicamentos(
			List<MpmPrescricaoMdto> prescricaoMedicamentos, String nomeMicrocomputador,List<MpmPrescricaoMdto> prescricaoMedicamentosOriginal) throws BaseException, BaseRuntimeException {
		this.persistirPrescricaoMedicamentos(prescricaoMedicamentos, false, nomeMicrocomputador,prescricaoMedicamentosOriginal);
	}
	
	public void persistirPrescricaoMedicamentos(
			List<MpmPrescricaoMdto> prescricaoMedicamentos, Boolean isCopiado,
			String nomeMicrocomputador,List<MpmPrescricaoMdto> prescricaoMedicamentosOriginal) throws BaseException, BaseRuntimeException {
		
		//for (MpmPrescricaoMdto medicamento : prescricaoMedicamentos){
		for (int i =0; i < prescricaoMedicamentos.size();i++){
			MpmPrescricaoMdto medicamento = prescricaoMedicamentos.get(i);
			MpmPrescricaoMdto medicamentoOriginal = prescricaoMedicamentosOriginal.get(i);
			this.persistirPrescricaoMedicamento(medicamento, isCopiado, nomeMicrocomputador,medicamentoOriginal);
		}
	}

	public void persistirParecerItemPrescricaoMedicamento(
			MpmItemPrescParecerMdto itemParecer) throws ApplicationBusinessException {
		getPrescricaoMedicamentoRN().inserirParecerItemPrescricaoMedicamento(
				itemParecer);
	}

	protected MpmItemPrescricaoMdtoDAO getMpmItemPrescricaoMdtoDAO() {
		return mpmItemPrescricaoMdtoDAO;
	}

	public void removerPrescricaoMedicamento(
			MpmPrescricaoMedica prescricaoMedica,
			MpmPrescricaoMdto prescicaoMedicamento, 
			String nomeMicrocomputador, MpmPrescricaoMdto prescricaoMedicamentoOriginal) throws BaseException {
		if (DominioIndPendenteItemPrescricao.B.equals(prescicaoMedicamento
				.getIndPendente())) {
			this.excluirPrescricaoMedicamentoReatachar(prescicaoMedicamento);
		} else if (DominioIndPendenteItemPrescricao.R
				.equals(prescicaoMedicamento.getIndPendente())) {
			prescicaoMedicamento
					.setIndPendente(DominioIndPendenteItemPrescricao.Y);
			prescicaoMedicamento.setDthrFim(prescicaoMedicamento
					.getDthrInicio());
			this.persistirPrescricaoMedicamento(prescicaoMedicamento, nomeMicrocomputador,prescricaoMedicamentoOriginal);
		} else if (DominioIndPendenteItemPrescricao.N
				.equals(prescicaoMedicamento.getIndPendente())) {
			prescicaoMedicamento
					.setIndPendente(DominioIndPendenteItemPrescricao.E);
			prescicaoMedicamento.setDthrFim(this.isPrescricaoVigente(
					prescricaoMedica.getDthrInicio(), prescricaoMedica
							.getDthrFim()) ? prescricaoMedica
					.getDthrMovimento() : prescricaoMedica.getDthrInicio());
			this.persistirPrescricaoMedicamento(prescicaoMedicamento, nomeMicrocomputador,prescricaoMedicamentoOriginal);
		} else if (DominioIndPendenteItemPrescricao.P
				.equals(prescicaoMedicamento.getIndPendente())) {
			if (prescicaoMedicamento.getPrescricaoMdtoOrigem() == null) {
				this.excluirPrescricaoMedicamentoReatachar(prescicaoMedicamento);
			} else {
				MpmPrescricaoMdto autoRelacionamento = prescicaoMedicamento
						.getPrescricaoMdtoOrigem();
				this.excluirPrescricaoMedicamentoReatachar(prescicaoMedicamento);
				autoRelacionamento
						.setIndPendente(DominioIndPendenteItemPrescricao.E);
				this.persistirPrescricaoMedicamento(autoRelacionamento, nomeMicrocomputador,prescricaoMedicamentoOriginal);
			}
		}
	}

	public Boolean isPrescricaoVigente(Date dthrInicio, Date dthrFim)
			throws ApplicationBusinessException {
		Long dthrAtualTime = new Date().getTime();

		if (dthrAtualTime >= dthrInicio.getTime()
				&& dthrAtualTime < dthrFim.getTime()) {
			return true;
		} else if (dthrAtualTime < dthrInicio.getTime()) {
			return false;
		} else {
			throw new ApplicationBusinessException(
					ManterPrescricaoMedicamentoExceptionCode.PERIODO_PRESCRICAO_MEDICAMENTO_INVALIDO,
					DATE_FORMAT.format(dthrInicio), DATE_FORMAT
							.format(dthrInicio));
		}
	}

	public MpmPrescricaoMdto obterPrescricaoMedicamentoDetalhado(Integer atdSeq, Long seq) {
		return mpmPrescricaoMdtoDAO.obterMpmPrescricaoMdtoPorPK(new MpmPrescricaoMdtoId(atdSeq, seq));
	}
	
	public void excluirPrescricaoMedicamentoReatachar(MpmPrescricaoMdto prescricaoMedicamento) 
			throws ApplicationBusinessException {
		prescricaoMedicamento = getMpmPrescricaoMdtoDAO().merge(prescricaoMedicamento);
		try {
			Boolean autoExcluirProcedimentoMedicamentoSol  = Boolean.TRUE;
			if (prescricaoMedicamento.getItensPrescricaoMdtos() != null) {
				for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
						.getItensPrescricaoMdtos()) {
					getItemPrescricaoMedicamentoRN().excluirItemPrescricaoMedicamento(itemPrescricaoMedicamento, autoExcluirProcedimentoMedicamentoSol);
				}
			}
			
			getPrescricaoMedicamentoRN().excluirPrescricaoMedicamento(
					prescricaoMedicamento);

			getPrescricaoMedicamentoRN().flush();
			
		} catch(ApplicationBusinessException | BaseRuntimeException  e){
			LOG.error("Erro", e);
			throw e;
		}
	}
	
	public void persistirPrescricaoMedicamentoCancelar(
			MpmPrescricaoMdto prescricaoMedicamento, Boolean isCopiado, String nomeMicrocomputador
			, MpmPrescricaoMdto prescricaoMedicamentoOriginal)
			throws BaseException {
			Boolean autoExcluirProcedimentoMedicamentoSol = null;
			
			prescricaoMedicamento = getMpmPrescricaoMdtoDAO().merge(prescricaoMedicamento);
			
			ItemPrescricaoMedicamentoRN itemPrescricaoMedicamentoRN = getItemPrescricaoMedicamentoRN();
			
			List<ItemPrescricaoMedicamentoVO> listaItensVO = getMpmItemPrescricaoMdtoDAO().obterListaItemPrescricaoMdtoVO(prescricaoMedicamento.getPrescricaoMedica().getId().getAtdSeq(), prescricaoMedicamento.getId().getSeq());
			boolean remove = true;
			
			if (prescricaoMedicamento.getItensPrescricaoMdtos() != null) {
				for ( Iterator<ItemPrescricaoMedicamentoVO> itr = listaItensVO.iterator(); itr.hasNext(); ) {					
					ItemPrescricaoMedicamentoVO vo = itr.next();
					remove = false;
					for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
							.getItensPrescricaoMdtos()) {
							if(itemPrescricaoMedicamento.getId() != null && vo.getMatCodigo().equals(itemPrescricaoMedicamento.getId().getMedMatCodigo())) {
								remove = true;
								break;
							}
					}
					if(remove) {
						itr.remove();
					}
				}
			}

			for(ItemPrescricaoMedicamentoVO vo : listaItensVO) {
				MpmItemPrescricaoMdto item = getMpmItemPrescricaoMdtoDAO().obterPorChavePrimaria(new MpmItemPrescricaoMdtoId(vo.getPmdAtdSeq(), vo.getPmdSeq(), vo.getMatCodigo(), vo.getSeqp()));
				autoExcluirProcedimentoMedicamentoSol = Boolean.FALSE;
				getItemPrescricaoMedicamentoRN().excluirItemPrescricaoMedicamento(item, autoExcluirProcedimentoMedicamentoSol);
			}
							
			if (prescricaoMedicamento.getItensPrescricaoMdtos() != null) {
				for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
						.getItensPrescricaoMdtos()) {
					if(itemPrescricaoMedicamento.getId() == null || itemPrescricaoMedicamento.getId().getSeqp() == null) {
						MpmItemPrescricaoMdtoId idItem = new MpmItemPrescricaoMdtoId();
						idItem.setPmdAtdSeq(prescricaoMedicamento.getPrescricaoMedica().getId().getAtdSeq());
						idItem.setPmdSeq(prescricaoMedicamento.getId().getSeq());
						idItem.setMedMatCodigo(itemPrescricaoMedicamento
								.getMedicamento().getMatCodigo());
						idItem.setSeqp((short) 1);
						
						itemPrescricaoMedicamento.setId(idItem);
	
						itemPrescricaoMedicamentoRN
								.inserirItemPrescricaoMedicamento(itemPrescricaoMedicamento);
					} else {
						itemPrescricaoMedicamentoRN
								.atualizarItemPrescricaoMedicamento(itemPrescricaoMedicamento);
					}
				}
			}
			
			getPrescricaoMedicamentoRN().atualizarPrescricaoMedicamento(
					prescricaoMedicamento, nomeMicrocomputador, prescricaoMedicamentoOriginal,autoExcluirProcedimentoMedicamentoSol);

			getPrescricaoMedicamentoRN().flush();
		}
}
package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgAlergiasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgExameDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.controlepaciente.service.IControlePacienteService;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioTipoDadoParametro;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamOrigemPacienteDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGeralDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgMedicacaoDAO;
import br.gov.mec.aghu.emergencia.dao.MamTriagensJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendemDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidXExameDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidXGeralDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidXMedicacaoDAO;
import br.gov.mec.aghu.emergencia.vo.MamTriagemVO;
import br.gov.mec.aghu.emergencia.vo.TriagemVO;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.model.MamTrgAlergias;
import br.gov.mec.aghu.model.MamTrgAlergiasId;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.service.ServiceException;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
/**
 * Regras de negócio relacionadas ao acolhimento do Paciente.
 * 
 * @author ihaas
 * 
 */
@Stateless
public class RealizarAcolhimentoON extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@EJB
	private IPacienteService pacienteService;
	
	@EJB
	private IControlePacienteService controlePacienteService;
	
	@EJB
	private IPrescricaoMedicaService prescricaoMedicaService;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamOrigemPacienteDAO mamOrigemPacienteDAO;
	
	@Inject
	private MamUnidAtendemDAO mamUnidAtendemDAO;
	
	@Inject
	private MamTrgGravidadeDAO mamTrgGravidadeDAO;
	
	@Inject
	private MamTriagensJnDAO mamTriagensJnDAO;
	
	@Inject
	private MamTrgAlergiasDAO mamTrgAlergiasDAO;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@Inject
	private MamUnidXGeralDAO mamUnidXGeralDAO;
	
	@Inject
	private MamTrgGeralDAO mamTrgGeralDAO;
	
	@Inject
	private MamUnidXExameDAO mamUnidXExameDAO;
	
	@Inject
	private MamTrgExameDAO mamTrgExameDAO;
	
	@Inject
	private MamUnidXMedicacaoDAO mamUnidXMedicacaoDAO;
	
	@Inject
	private MamTrgMedicacaoDAO mamTrgMedicacaoDAO;
		
	@Inject
	private MamObrigatoriedadeDAO mamObrigatoriedadeDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@EJB
	private MamTriagensRN mamTriagensRN;
	
	@EJB
	private MamTrgAlergiasRN mamTrgAlergiasRN;
	
	@EJB
	private MarcarConsultaEmergenciaRN marcarConsultaEmergenciaRN;
	
	@EJB
	private MamTrgGeralRN mamTrgGeralRN;
	
	@EJB
	private MamTrgExameRN mamTrgExameRN;
	
	@EJB
	private MamTrgMedicacaoRN mamTrgMedicacaoRN;
	
	@Inject
	private AghUnidadesFuncionaisDAO unidadeDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum RealizarAcolhimentoONExceptionCode implements BusinessExceptionCode {
		MAM_02836, MAM_02812, MENSAGEM_SERVICO_INDISPONIVEL, MAM_ERRO_ITEM_DATA, MAM_ERRO_ITEM_NUMERICO, MAM_ERRO_ITEM_NUMERO_ENTRE,
		MAM_ERRO_ITEM_GERAL_OBRIGATORIO, MAM_ERRO_ITEM_EXAME_OBRIGATORIO, MAM_ERRO_ITEM_MEDICACAO_OBRIGATORIO,
		MAM_ERRO_ITEM_SINAL_VITAL_OBRIGATORIO, ERRO_CONSULTA_SEM_TRIAGEM
	}
	
	public MamTriagemVO obterTriagemVOPorSeq(Long trgSeq, Integer pacCodigo) throws ApplicationBusinessException {
		
		TriagemVO vo = mamTriagensDAO.obterTriagemVOPorSeq(trgSeq);
		if (vo.getSeqOrigem() != null) {
			vo.setMamOrigemPaciente(mamOrigemPacienteDAO.obterPorChavePrimaria(vo.getSeqOrigem()));
		}
		vo.setMamUnidAtendem(mamUnidAtendemDAO.obterPorChavePrimaria(vo.getUnfSeq()));
		vo.setIndObrOrgPaciente(vo.getMamUnidAtendem().getIndObrOrgPaciente());
		
		Paciente paciente = obterPacientePorCodigo(pacCodigo);
		vo.setPacCodigo(pacCodigo);
		vo.setNomePaciente(paciente.getNome());
		vo.setInformacoesPaciente(preencherInformacoesPaciente(paciente));
		
		List<Short> unfSeqTriagensJn = mamTriagensJnDAO.obterUnidadeTriagensJnPorTrgSeq(trgSeq);
		if (unfSeqTriagensJn != null && !unfSeqTriagensJn.isEmpty() && unfSeqTriagensJn.size() > 1) {
			if (!unfSeqTriagensJn.get(1).equals(vo.getUnfSeq())) {
				vo.setCodCor(null);
			}
		}
		// #29973
		vo.setIndDataQueixaObr(mamTrgGravidadeDAO.verificaObrigatoriedadeDataHoraQueixa(trgSeq, true));
		vo.setIndHoraQueixaObr(mamTrgGravidadeDAO.verificaObrigatoriedadeDataHoraQueixa(trgSeq, false));
		
		MamTriagemVO triagemVO = new MamTriagemVO();
		try {
			PropertyUtils.copyProperties(triagemVO, vo);
		} catch (Exception e) {
			throw new RuntimeException( //NOPMD
					"Não foi possivel fazer a cópia dos objetos da classe TriagemVO.",
					e);
		}
		return triagemVO;
	}
	
	public List<PostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) throws ApplicationBusinessException {
		
		List<PostoSaude> listaRetorno = new ArrayList<PostoSaude>();
		
		try {
			listaRetorno = this.prescricaoMedicaService.listarMpmPostoSaudePorSeqDescricao(parametro);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listaRetorno;
	}
	
	public Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) throws ApplicationBusinessException {
		
		Long result = null;
		
		try {
			result = this.prescricaoMedicaService.listarMpmPostoSaudePorSeqDescricaoCount(parametro);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	public PostoSaude obterPostoSaudePorChavePrimaria(Integer seq) throws ApplicationBusinessException {
		PostoSaude result = null;
		
		try {
			result = this.prescricaoMedicaService.obterPostoSaudePorChavePrimaria(seq);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	/**
	 * Grava os dados do acolhimento.
	 * @param vo
	 * @param validarFluxograma Só deve validar caso tenha sido chamado pelo botão Gravar.
	 * @return true se deve exibir mensagem MAM_ERRO_SERVICO_CONTROLE_PACIENTE_INDISPONIVEL
	 * @throws ApplicationBusinessException 
	 */
	public Boolean gravarDadosAcolhimento(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal,
			boolean validarFluxograma, String hostName) throws ApplicationBusinessException {
		
		Boolean isServicoCPIndisponivel = false;
		boolean isFluxogramaOK = true;
		if (validarFluxograma) {
			isFluxogramaOK = this.mamTrgGravidadeDAO.verificarExisteGravidadePorTriagem(vo.getTrgSeq());
		}
		
		if (isFluxogramaOK) {
			MamTriagens triagem = this.mamTriagensDAO.obterPorChavePrimaria(vo.getTrgSeq());
			MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(triagem.getSeq());
			
			this.mamTriagensRN.atualizarTriagemAcolhimento(triagem, mamTriagemOriginal, vo);
			
			if ((mamTriagemVOOriginal.getAlergias() == null || mamTriagemVOOriginal.getAlergias().equals(""))
					&& vo.getAlergias() != null && !vo.getAlergias().equals("")) {
				
				this.mamTrgAlergiasRN.inserirTrgAlergias(vo, hostName);
				
			} else if (vo.getAlergias() != null && !vo.getAlergias().equals("")
					&& CoreUtil.modificados(vo.getAlergias(), mamTriagemVOOriginal.getAlergias())) {
				
				MamTrgAlergiasId id = new MamTrgAlergiasId(vo.getTrgSeq(), vo.getSeqpAlergia());
				MamTrgAlergias alergia = this.mamTrgAlergiasDAO.obterPorChavePrimaria(id);
				MamTrgAlergias mamTrgAlergiasOriginal = this.mamTrgAlergiasDAO.obterOriginal(alergia);
				this.mamTrgAlergiasRN.atualizarAlergia(alergia, mamTrgAlergiasOriginal, vo);
			}
			
		} else {
			throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_02812);
		}
		// 29973
		if (validarFluxograma) {
			validarEPersistirDadosGerais(vo.getListMamTrgGerais(), true, vo.getTrgSeq());
			validarEPersistirExames(vo.getListMamTrgExames(), true, vo.getTrgSeq());
			validarEPersistirMedicacoes(vo.getListMamTrgMedicacoes(), true, vo.getTrgSeq());
			validarObrigatoriedadeItens(vo, isServicoCPIndisponivel);
		}
		return isServicoCPIndisponivel;
	}
	
	private void validarObrigatoriedadeItens(MamTriagemVO vo, Boolean isServicoCPIndisponivel) throws ApplicationBusinessException {
		List<MamObrigatoriedade> itensObrigatorios = this.mamObrigatoriedadeDAO.pesquisarItensObrigatoriosPorTriagem(vo.getTrgSeq());
		
		for (MamObrigatoriedade itemObrigatorio : itensObrigatorios) {
			
			if (itemObrigatorio.getMamItemGeral() != null) {
				validarItemGeralObrigatorio(itemObrigatorio.getMamItemGeral().getSeq(), vo.getListMamTrgGerais());
			}
			if (itemObrigatorio.getMamItemExame() != null) {
				validarItemExameObrigatorio(itemObrigatorio.getMamItemExame().getSeq(), vo.getListMamTrgExames());
			}
			if (itemObrigatorio.getMamItemMedicacao() != null) {
				validarItemMedicacaoObrigatorio(itemObrigatorio.getMamItemMedicacao().getSeq(), vo.getListMamTrgMedicacoes());
			}
			if (itemObrigatorio.getIceSeq() != null) {
				if (verificarExisteSinalVitalPorPaciente(itemObrigatorio.getIceSeq(), vo.getPacCodigo(),
						isServicoCPIndisponivel).equals(Boolean.FALSE)) {
					
					String descricaoSinalVital = obterDescricaoItemControle(itemObrigatorio.getIceSeq(), isServicoCPIndisponivel);
					if (isServicoCPIndisponivel.equals(Boolean.FALSE)) {
						throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_SINAL_VITAL_OBRIGATORIO,
								descricaoSinalVital);
					}
				}
			}
		}
		List<Integer> listItemGerailObrigatorios = this.mamUnidXGeralDAO.obterListaSeqUnidXGeralObrigatorios(vo.getTrgSeq());
		for (Integer itgSeq : listItemGerailObrigatorios) {
			validarItemGeralObrigatorio(itgSeq, vo.getListMamTrgGerais());
		}
		
		List<Integer> listItemExameObrigatorios = this.mamUnidXExameDAO.obterListaSeqUnidXExameObrigatorios(vo.getTrgSeq());
		for (Integer emsSeq : listItemExameObrigatorios) {
			validarItemExameObrigatorio(emsSeq, vo.getListMamTrgExames());
		}
		
		List<Integer> listItemMedicacaoObrigatorios = this.mamUnidXMedicacaoDAO.obterListaSeqUnidXMedicacaoObrigatorios(vo.getTrgSeq());
		for (Integer mdmSeq : listItemMedicacaoObrigatorios) {
			validarItemMedicacaoObrigatorio(mdmSeq, vo.getListMamTrgMedicacoes());
		}
	}
	
	public Boolean verificarExisteSinalVitalPorPaciente(Short iceSeq, Integer pacCodigo, Boolean isServicoCPIndisponivel) throws ApplicationBusinessException {
		Boolean result = false;
		
		try {
			result = this.controlePacienteService.verificarExisteSinalVitalPorPaciente(iceSeq, pacCodigo);
			
		} catch (ServiceException e) {
			isServicoCPIndisponivel = true;
			result = true;
		}
		return result;
	}
	
	public String obterDescricaoItemControle(Short iceSeq, Boolean isServicoCPIndisponivel) throws ApplicationBusinessException {
		String result = "";
		
		try {
			result = this.controlePacienteService.obterDescricaoItemControle(iceSeq);
			
		} catch (ServiceException e) {
			isServicoCPIndisponivel = true;
		}
		return result;
	}
	
	private void validarItemGeralObrigatorio(Integer itgSeq, List<MamTrgGerais> listMamTrgGerais) throws ApplicationBusinessException {
		for (MamTrgGerais item : listMamTrgGerais) {
			if (item.getMamItemGeral().getSeq().equals(itgSeq) && item.getComplemento() == null) {
				throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_GERAL_OBRIGATORIO,
						item.getMamItemGeral().getDescricao());
			}
		}
	}
	
	private void validarItemExameObrigatorio(Integer emsSeq, List<MamTrgExames> listMamTrgExames) throws ApplicationBusinessException {
		for (MamTrgExames item : listMamTrgExames) {
			if (item.getItemExame().getSeq().equals(emsSeq) && item.getComplemento() == null) {
				throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_EXAME_OBRIGATORIO,
						item.getItemExame().getDescricao());
			}
		}
	}
	
	private void validarItemMedicacaoObrigatorio(Integer mdmSeq, List<MamTrgMedicacoes> listMamTrgMedicacoes)
			throws ApplicationBusinessException {
		
		for (MamTrgMedicacoes item : listMamTrgMedicacoes) {
			if (item.getItemMedicacao().getSeq().equals(mdmSeq) && item.getComplemento() == null) {
				throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_MEDICACAO_OBRIGATORIO,
						item.getItemMedicacao().getDescricao());
			}
		}
	}
	
	/**
	 * 
	 * @param listMamTrgGerais
	 * @param isGravar indica se o método foi chamado pelo botão Gravar
	 * @param trgSeq
	 * @throws ApplicationBusinessException
	 */
	private void validarEPersistirDadosGerais(List<MamTrgGerais> listMamTrgGerais,
			boolean isGravar, Long trgSeq) throws ApplicationBusinessException {
		for (MamTrgGerais item : listMamTrgGerais) {
			
			MamTrgGerais itemOriginal = this.mamTrgGeralDAO.obterOriginal(item.getId());
			
			if (isGravar && item.getIndConsistenciaOk().equals(Boolean.FALSE)) {
				item.setIndConsistenciaOk(Boolean.TRUE);
				item.setDtHrConsistenciaOk(new Date());
			}
			// Feito isso pois o CoreUtil considera "" diferente de null.
			if (item.getComplemento() != null && item.getComplemento().equals("")) {
				item.setComplemento(null);
			}
			if (CoreUtil.modificados(item.getComplemento(), itemOriginal.getComplemento())) {
				
				DominioTipoDadoParametro tipoDado = item.getMamItemGeral().getTipoDado();
				String complemento = item.getComplemento();
				String itemDescricao = item.getMamItemGeral().getDescricao();
				BigDecimal vlrMinimo = item.getMamItemGeral().getValorMinimo();
				BigDecimal vlrMaximo = item.getMamItemGeral().getValorMaximo();
				
				validarItem(tipoDado, complemento, itemDescricao, vlrMinimo, vlrMaximo);
				
				this.mamTrgGeralRN.atualizarTrgGeral(item, itemOriginal);
			}
			if (isGravar) {
				if (this.mamUnidXGeralDAO.verificaComplementoGeralObrigatorio(item.getMamItemGeral().getSeq(), trgSeq)
						&& item.getComplemento() == null) {
					
					throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_GERAL_OBRIGATORIO,
							item.getMamItemGeral().getDescricao());
				}
			}
				
		}
	}
	
	/**
	 * 
	 * @param listMamTrgExames
	 * @param isGravar indica se o método foi chamado pelo botão Gravar
	 * @param trgSeq
	 * @throws ApplicationBusinessException
	 */
	private void validarEPersistirExames(List<MamTrgExames> listMamTrgExames,
			boolean isGravar, Long trgSeq) throws ApplicationBusinessException {
		for (MamTrgExames item : listMamTrgExames) {
			
			MamTrgExames itemOriginal = this.mamTrgExameDAO.obterOriginal(item.getId());
			
			if (isGravar && item.getIndConsistenciaOk().equals(Boolean.FALSE)) {
				item.setIndConsistenciaOk(Boolean.TRUE);
				item.setDthrConsistenciaOk(new Date());
			}
			// Feito isso pois o CoreUtil considera "" diferente de null.
			if (item.getComplemento() != null && item.getComplemento().equals("")) {
				item.setComplemento(null);
			}
			if (CoreUtil.modificados(item.getComplemento(), itemOriginal.getComplemento())) {
				
				DominioTipoDadoParametro tipoDado = item.getItemExame().getTipoDado();
				String complemento = item.getComplemento();
				String itemDescricao = item.getItemExame().getDescricao();
				BigDecimal vlrMinimo = item.getItemExame().getValorMinimo();
				BigDecimal vlrMaximo = item.getItemExame().getValorMaximo();
				
				validarItem(tipoDado, complemento, itemDescricao, vlrMinimo, vlrMaximo);
				
				this.mamTrgExameRN.atualizarTrgExame(item, itemOriginal);
			}
			if (isGravar) {
				if (this.mamUnidXExameDAO.verificaComplementoExameObrigatorio(item.getItemExame().getSeq(), trgSeq)
						&& item.getComplemento() == null) {
					
					throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_EXAME_OBRIGATORIO,
							item.getItemExame().getDescricao());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param listMamTrgMedicacoes
	 * @param isGravar indica se o método foi chamado pelo botão Gravar
	 * @param trgSeq
	 * @throws ApplicationBusinessException
	 */
	private void validarEPersistirMedicacoes(List<MamTrgMedicacoes> listMamTrgMedicacoes,
			boolean isGravar, Long trgSeq) throws ApplicationBusinessException {
		for (MamTrgMedicacoes item : listMamTrgMedicacoes) {
			
			MamTrgMedicacoes itemOriginal = this.mamTrgMedicacaoDAO.obterOriginal(item.getId());
			
			if (isGravar && item.getConsistenciaOk().equals(Boolean.FALSE)) {
				item.setConsistenciaOk(Boolean.TRUE);
				item.setDthrConsistenciaOk(new Date());
			}
			// Feito isso pois o CoreUtil considera "" diferente de null.
			if (item.getComplemento() != null && item.getComplemento().equals("")) {
				item.setComplemento(null);
			}
			if (CoreUtil.modificados(item.getComplemento(), itemOriginal.getComplemento())) {
				
				DominioTipoDadoParametro tipoDado = item.getItemMedicacao().getTipoDado();
				String complemento = item.getComplemento();
				String itemDescricao = item.getItemMedicacao().getDescricao();
				BigDecimal vlrMinimo = item.getItemMedicacao().getValorMinimo();
				BigDecimal vlrMaximo = item.getItemMedicacao().getValorMaximo();
				
				validarItem(tipoDado, complemento, itemDescricao, vlrMinimo, vlrMaximo);
				
				this.mamTrgMedicacaoRN.atualizarTrgMedicacao(item, itemOriginal);
			}
			if (isGravar) {
				if (this.mamUnidXMedicacaoDAO.verificaComplementoMedicacaoObrigatorio(item.getItemMedicacao().getSeq(), trgSeq)
						&& item.getComplemento() == null) {
					
					throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_MEDICACAO_OBRIGATORIO,
							item.getItemMedicacao().getDescricao());
				}
			}
		}
	}
	
	private void validarItem(DominioTipoDadoParametro tipoDado, String complemento, String itemDescricao,
			BigDecimal vlrMinimo, BigDecimal vlrMaximo) throws ApplicationBusinessException {
		
		if (DominioTipoDadoParametro.D.equals(tipoDado)) {
			final SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
			sf.setLenient(false);
			
			try {
				sf.parse(complemento);
			} catch (ParseException e) {
				throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_DATA, itemDescricao);
			}
		} else if (DominioTipoDadoParametro.N.equals(tipoDado)) {
			if (!StringUtils.isNumeric(complemento)) {
				throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_NUMERICO, itemDescricao);
			}
			if (vlrMinimo != null && vlrMaximo != null
					&& (new BigDecimal(complemento).compareTo(vlrMinimo) < 0 || new BigDecimal(complemento).compareTo(vlrMaximo) > 0)) {
				
				throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_ERRO_ITEM_NUMERO_ENTRE, itemDescricao,
						vlrMinimo, vlrMaximo);
			}
		}
	}
	
	public void transferirPacienteUnidade(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal,
			String hostName, Paciente paciente) throws ApplicationBusinessException {
		MamTriagens triagem = this.mamTriagensDAO.obterPorChavePrimaria(vo.getTrgSeq());
		MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(triagem.getSeq());
		
		triagem.setUnidadeFuncional(unidadeDAO.obterPorUnfSeq(this.obterUnfSeqByIdade(paciente, vo.getMamUnidAtendem()))); //RN015 - melhoria 39632
		
		this.mamTriagensRN.atualizarTriagemPorTipoMovimento(triagem, mamTriagemOriginal, DominioTipoMovimento.E, hostName);
		// 29973
		validarEPersistirDadosGerais(vo.getListMamTrgGerais(), false, vo.getTrgSeq());
		validarEPersistirExames(vo.getListMamTrgExames(), false, vo.getTrgSeq());
		validarEPersistirMedicacoes(vo.getListMamTrgMedicacoes(), false, vo.getTrgSeq());
		
		if (isMamTriagemAlterada(mamTriagemVOOriginal, vo)) {
			gravarDadosAcolhimento(vo, mamTriagemVOOriginal, false, hostName);
		}
	}
	
	private Short obterUnfSeqByIdade(Paciente paciente, MamUnidAtendem unidade) throws ApplicationBusinessException {
		Short seqUnidadeDestino = unidade.getUnfSeq();
		if (unidade.getIndDivideIdade().equals(Boolean.TRUE)) {
			
			int idade = CoreUtil.calculaIdade(paciente.getDtNascimento());
			BigDecimal paramIdade=null;
			paramIdade = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_IDADE_TRG_PEDIATRICA.toString(), "vlrNumerico");
			
			if (CoreUtil.menorOuIgual(idade, paramIdade)) {
				
				BigDecimal paramUnidPediatrica=null;
				paramUnidPediatrica = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_UNF_EME_PEDIATRICA.toString(), "vlrNumerico");
				seqUnidadeDestino = paramUnidPediatrica.shortValue();
				
			} else {
				
				BigDecimal paramUnidAdulto=null;
				paramUnidAdulto = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_UNF_EME_ADULTO.toString(), "vlrNumerico");				
				seqUnidadeDestino = paramUnidAdulto.shortValue();
			}
		}
		return seqUnidadeDestino;
	}

	public void naoTransferirPacienteUnidade(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal,
			String hostName) throws ApplicationBusinessException {
		
		MamTriagens triagem = this.mamTriagensDAO.obterPorChavePrimaria(vo.getTrgSeq());
		MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(triagem.getSeq());
		
		if (!mamCaractSitEmergDAO.isExisteSituacaoEmerg(triagem.getSituacaoEmergencia().getSeq(),
				DominioCaracteristicaEmergencia.LISTA_TRIAGEM)) {
			
			throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MAM_02836);
		}
		List<Short> segSeq = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.PERMANECE_TRIAGEM);
		triagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq.get(0)));
		
		this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(triagem, mamTriagemOriginal, hostName);
		// 29973
		validarEPersistirDadosGerais(vo.getListMamTrgGerais(), false, vo.getTrgSeq());
		validarEPersistirExames(vo.getListMamTrgExames(), false, vo.getTrgSeq());
		validarEPersistirMedicacoes(vo.getListMamTrgMedicacoes(), false, vo.getTrgSeq());
		
		if (isMamTriagemAlterada(mamTriagemVOOriginal, vo)) {
			gravarDadosAcolhimento(vo, mamTriagemVOOriginal, false, hostName);
		}
	}
	
	private String preencherInformacoesPaciente(Paciente paciente) {
		StringBuilder strRetorno = new StringBuilder(100);

		strRetorno
				.append(paciente.getNome())
				.append(" - ")
				.append(obterIdadePorDataNascimento(paciente.getDtNascimento()))
				.append("- ").append("Prontuário:")
				.append(CoreUtil.formataProntuario(paciente.getProntuario()));
	

		return strRetorno.toString();
	}
	
	public Paciente obterPacientePorCodigo(Integer pacCodigo) throws ApplicationBusinessException {
		Paciente result = null;
		PacienteFiltro filtro = new PacienteFiltro();
		filtro.setCodigo(pacCodigo);
		
		try {
			result = this.pacienteService.obterPacientePorCodigoOuProntuario(filtro);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	/**
	 * Calcula a idade por extenso de um paciente a partir
	 * da data de nascimento.
	 * 
	 * ORADB: MPMC_IDA_ANO_MES_DIA
	 * 
	 * @param dtNascimentoPaciente
	 * @return
	 */
	public String obterIdadePorDataNascimento(Date dtNascimentoPaciente) {
		Calendar calAtual = Calendar.getInstance();

		Date dataAtualTruncada = DateUtil.truncaData(calAtual.getTime());
		Date dataNascimentoTruncada = DateUtil.truncaData(dtNascimentoPaciente);
		
		Calendar calDtUltimoAniversario = Calendar.getInstance();
		calDtUltimoAniversario.setTime(dataNascimentoTruncada);		
		calDtUltimoAniversario.set(Calendar.YEAR, calAtual.get(Calendar.YEAR));		
		
		// Caso o paciente ainda não tenha feito aniversário no ano considerando a data atual
		if (calDtUltimoAniversario.getTime().after(dataAtualTruncada)) {
			calDtUltimoAniversario.set(Calendar.YEAR, calDtUltimoAniversario.get(Calendar.YEAR) - 1);
		}

		Integer anos = DateUtil.obterQtdAnosEntreDuasDatas(dataNascimentoTruncada, dataAtualTruncada);
		Integer meses = DateUtil.obterQtdMesesEntreDuasDatas(calDtUltimoAniversario.getTime(), dataAtualTruncada);
		Integer dias = DateUtil.obterQtdDiasEntreDuasDatas(calDtUltimoAniversario.getTime(), dataAtualTruncada);
		
		StringBuilder strIdade = new StringBuilder();
		
		if (anos == 0 && meses == 0) {
			concatenarIdadeDias(strIdade, dias);
			
		} else if (anos == 0) {
			Calendar calDtNascimentoMesAtual = Calendar.getInstance();
			calDtNascimentoMesAtual.setTime(dataNascimentoTruncada);		
			calDtNascimentoMesAtual.set(Calendar.MONTH, calAtual.get(Calendar.MONTH));
			
			dias = DateUtil.obterQtdDiasEntreDuasDatas(calDtNascimentoMesAtual.getTime(), dataAtualTruncada);
			
			concatenarIdadeMeses(strIdade, meses);
			concatenarIdadeDias(strIdade, dias);
			
		} else if (anos >= 1) {
			if (anos > 1) {
				strIdade.append(anos).append(" anos ");
			} else if (anos == 1) {
				strIdade.append(anos).append(" ano ");
			}
			concatenarIdadeMeses(strIdade, meses);
		}

		return strIdade.toString();
	}
	
	private void concatenarIdadeDias(StringBuilder strIdade, Integer dias) {
		if (dias > 1) {
			strIdade.append(dias).append(" dias ");
		} else if (dias == 1) {
			strIdade.append(dias).append(" dia ");
		}
	}
	
	private void concatenarIdadeMeses(StringBuilder strIdade, Integer meses) {
		if (meses > 1) {
			strIdade.append(meses).append(" meses ");
		} else if (meses == 1) {
			strIdade.append(meses).append(" mês ");
		}
	}
	
	private boolean isMamTriagemAlterada(MamTriagemVO mamTriagemOriginal, MamTriagemVO mamTriagem) {
		return CoreUtil.modificados(mamTriagem.getNomePaciente(), mamTriagemOriginal.getNomePaciente()) ||
				CoreUtil.modificados(mamTriagem.getQueixaPrincipal(), mamTriagemOriginal.getQueixaPrincipal()) ||	
				CoreUtil.modificados(mamTriagem.getCodCor(), mamTriagemOriginal.getCodCor()) ||
				CoreUtil.modificados(mamTriagem.getDescricaoGravidade(), mamTriagemOriginal.getDescricaoGravidade()) ||
				CoreUtil.modificados(mamTriagem.getInformacoesPaciente(), mamTriagemOriginal.getInformacoesPaciente()) ||
				CoreUtil.modificados(mamTriagem.getSeqOrigem(), mamTriagemOriginal.getSeqOrigem()) ||
				CoreUtil.modificados(mamTriagem.getMamOrigemPaciente(), mamTriagemOriginal.getMamOrigemPaciente()) ||
				CoreUtil.modificados(mamTriagem.getIndObrOrgPaciente(), mamTriagemOriginal.getIndObrOrgPaciente()) ||
				CoreUtil.modificados(mamTriagem.getHouveContato(), mamTriagemOriginal.getHouveContato()) ||
				CoreUtil.modificados(mamTriagem.getNomeContato(), mamTriagemOriginal.getNomeContato()) ||
				CoreUtil.modificados(mamTriagem.getInformacoesComplementares(), mamTriagemOriginal.getInformacoesComplementares()) ||
				CoreUtil.modificados(mamTriagem.getAlergias(), mamTriagemOriginal.getAlergias()) ||
				CoreUtil.modificados(mamTriagem.getUnfSeq(), mamTriagemOriginal.getUnfSeq()) ||
				CoreUtil.modificados(mamTriagem.getDataQueixa(), mamTriagemOriginal.getDataQueixa()) ||
				CoreUtil.modificados(mamTriagem.getHoraQueixa(), mamTriagemOriginal.getHoraQueixa()) ||
				CoreUtil.modificados(mamTriagem.getIndInternado(), mamTriagemOriginal.getIndInternado()) ||
				CoreUtil.modificados(mamTriagem.getSeqHospitalInternado(), mamTriagemOriginal.getSeqHospitalInternado());
	}
	
	public List<MamTrgGerais> listarDadosGerais(Long trgSeq, String nomeComputador) {
		
		List<Integer> listUnidXGeral = mamUnidXGeralDAO.listaSeqUnidXGeral(trgSeq);
		
		for (Integer itgSeq : listUnidXGeral) {
			this.mamTrgGeralRN.inserirTrgGeral(trgSeq, itgSeq, nomeComputador);
		}
		
		return this.mamTrgGeralDAO.listarMamTrgGeralPorTriagem(trgSeq);
	}
	
	public List<MamTrgExames> listarExames(Long trgSeq, String nomeComputador) {
		
		List<Integer> listUnidXExame = mamUnidXExameDAO.listaSeqUnidXExame(trgSeq);
		
		for (Integer emsSeq : listUnidXExame) {
			this.mamTrgExameRN.inserirTrgExame(trgSeq, emsSeq, nomeComputador);
		}
		
		return this.mamTrgExameDAO.listarMamTrgExamesPorTriagem(trgSeq);
	}
	
	public List<MamTrgMedicacoes> listarMedicacoes(Long trgSeq, String nomeComputador) {
		
		List<Integer> listUnidXMedicacao = mamUnidXMedicacaoDAO.listaSeqUnidXMedicacao(trgSeq);
		
		for (Integer mdmSeq : listUnidXMedicacao) {
			this.mamTrgMedicacaoRN.inserirTrgMedicacao(trgSeq, mdmSeq, nomeComputador);
		}
		
		return this.mamTrgMedicacaoDAO.listarMamTrgMedicacoesPorTriagem(trgSeq);
	}
	
	/**
	 * Cria uma mensagem descritiva do tempo máximo em espera
	 * 
	 * @param tempo
	 * @return
	 */
	public String criarMensagemTempoMaximoEspera(Date tempo) {
		StringBuilder result = new StringBuilder();
		if (tempo != null) {
			Integer horas = DateUtil.getHoras(tempo);
			Double minutos = DateUtil.getMinutos(tempo);
			result = result.append(" - ").append("Em até").append(' ');
			if (horas > 0) {
				result = result.append(horas).append(' ');
				if (horas == 1) {
					result = result.append("Hora");
				} else {
					result = result.append("Horas");
				}
			}
			if (minutos > 0) {
				if (horas > 0) {
					result = result.append(" e ");
				}
				result = result.append(minutos.intValue()).append(' ');
				if (minutos == 1) {
					result = result.append("Minuto");
				} else {
					result = result.append("Minutos");
				}
			}
		}
		return result.toString();
	}

	public Long obterTrgSeq(Integer numeroConsulta) throws ApplicationBusinessException {
		Long trgSeq = mamTrgEncInternoDAO.obterTrgSeq(numeroConsulta);
		if(trgSeq == null) {
			throw new ApplicationBusinessException(RealizarAcolhimentoONExceptionCode.ERRO_CONSULTA_SEM_TRIAGEM);
		}
		return trgSeq;
	}
}

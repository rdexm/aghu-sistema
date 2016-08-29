package br.gov.mec.aghu.paciente.prontuarioonline.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamPcControlePac;
import br.gov.mec.aghu.model.MamPcHorarioCtrl;
import br.gov.mec.aghu.model.MamPcIntItemParada;
import br.gov.mec.aghu.model.MamPcSumExameMasc;
import br.gov.mec.aghu.model.MamPcSumExameTab;
import br.gov.mec.aghu.model.MamPcSumMascCampoEdit;
import br.gov.mec.aghu.model.MamPcSumMascLinha;
import br.gov.mec.aghu.model.MamPcSumObservacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoControleVO;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoDetalhesExamesVO;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoExamesVO;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioParadaInternacaoON extends BaseBusiness {


@EJB
private RelatorioParadaInternacaoRN relatorioParadaInternacaoRN;

private static final Log LOG = LogFactory.getLog(RelatorioParadaInternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IExamesFacade examesFacade;

@EJB
private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 6401108689444012873L;

	private static final float ORDEM = 8;

	private static final String PATTERN_DD_MM_YYYY = "dd/MM/yyyy";
	
	private static final String PATTERN_HH_MM = "HH:mm";

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected RelatorioParadaInternacaoRN getSumarioParadaInternacaoAtualRN() {
		return relatorioParadaInternacaoRN;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}	
	
	public String obterNomeArquivoRelatorioSumarioParadaAtual(Integer atdSeq) throws ApplicationBusinessException {
		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);

		return getSumarioParadaInternacaoAtualRN().obterNomeArquivoPdfInternacaoPaciente(
				atendimento.getSeq(), null, null, atendimento.getUnidadeFuncional().getSeq(), null, null);
	}
	
	public Boolean habilitarImprimirParadaInternacao(Integer atdSeq) {
		Boolean habilitar = Boolean.FALSE;
		AghAtendimentos atd = getAghuFacade().obterAghAtendimentosPorSeq(atdSeq);
		if (atd != null && DominioPacAtendimento.S.equals(atd.getIndPacAtendimento())
				&& (DominioOrigemAtendimento.I.equals(atd.getOrigem()) 
						|| DominioOrigemAtendimento.U.equals(atd.getOrigem()) 
						|| DominioOrigemAtendimento.N.equals(atd.getOrigem()))) {
			if (!getPrescricaoMedicaFacade().pesquisarParadaInternacaoPorAtendimento(atd.getSeq()).isEmpty()) {
				habilitar = Boolean.TRUE;	
			}
		}
		return habilitar;
	}
	
	/**
	 * Q_SUE
	 * @param vo
	 */
	private void preencherDadosSumarioExameTabela(RelatorioParadaInternacaoVO vo) {
		MamPcSumExameTab sue = getPrescricaoMedicaFacade().obterSumarioExameTabela(vo.getAtdSeq(), vo.getDthrCriacao());
		if (sue != null) {
			vo.setPacCodigo(sue.getPacCodigo());
			vo.setRecemNascido(sue.getRecemNascido());
			vo.setExibirExamesDetalhesExames(Boolean.TRUE);
		} else {
			vo.setExibirExamesDetalhesExames(Boolean.FALSE);
		}
	}
	
	/**
	 * Q_SUE2
	 * @param vo
	 */
	private void preencherDadosSumarioExameTabela2(RelatorioParadaInternacaoVO vo) {
		List<MamPcSumExameTab> result = getPrescricaoMedicaFacade().pesquisarSumarioExameTabela(vo.getAtdSeq(), vo.getDthrCriacao(), vo.getPacCodigo(), vo.getRecemNascido());
		for (MamPcSumExameTab sue : result) {
			RelatorioParadaInternacaoExamesVO examesVO = new RelatorioParadaInternacaoExamesVO();
			examesVO.setNome(sue.getCalNomeSumario());
			examesVO.setData(DateUtil.dataToString(sue.getDthrEventoAreaExec(), PATTERN_DD_MM_YYYY));
			examesVO.setHora(DateUtil.dataToString(sue.getDthrEventoAreaExec(), PATTERN_HH_MM));
			examesVO.setDataEvento(sue.getDthrEventoAreaExec());
			if (sue.getReeValor().longValue() == Math.round(sue.getReeValor())) {
				examesVO.setValor(sue.getReeValor());
			} else {
				//TODO - VERIFICAR FORMATAÇÃO to_char(SUE2.REE_VALOR,'fm999990D999999')
				examesVO.setValor(sue.getReeValor());
			}
			//CF_IDENTIFICADORFormula
			examesVO.setIdentificador(obterIdentificador(vo.getAtdSeq(), vo.getDthrCriacao(), vo.getPacCodigo(), sue.getPertenceSumario(), sue.getDthrEventoAreaExec(), sue.getRecemNascido()));
			
			vo.getExames().add(examesVO);
		}
	}
	
	/**
	 * CF_IDENTIFICADORFormula
	 * @param atdSeq
	 * @param dthrCriacao
	 * @param pacCodigo
	 * @param pertenceSumario
	 * @param dthrEvento
	 * @param recemNascido
	 * @return
	 */
	private String obterIdentificador(Integer atdSeq, Date dthrCriacao, Integer pacCodigo, DominioSumarioExame pertenceSumario, Date dthrEvento, Boolean recemNascido) {
		StringBuilder sb = new StringBuilder();
		
		MamPcSumObservacao obs = getPrescricaoMedicaFacade().obterSumarioObservacao(atdSeq, dthrCriacao, pacCodigo, pertenceSumario, dthrEvento, recemNascido);
		
		if (obs != null) {
			sb.append('(').append(obs.getCodigoMensagem()).append(')');
		}
		
		return sb.toString();
	}

	/**
	 * Q_MASC_LINHA
	 * @param vo
	 */
	private void preencherDadosSumarioMascLinha(RelatorioParadaInternacaoDetalhesExamesVO vo) {
		List<MamPcSumMascLinha> result = getPrescricaoMedicaFacade().pesquisarSumarioMascLinha(vo.getAtdSeq(), vo.getDthrCriacao(), vo.getOrdemRelatorio());
		StringBuilder mascLinha = new StringBuilder();
		StringBuilder longMasc = new StringBuilder();
		for (MamPcSumMascLinha sml : result) {
			if (StringUtils.isNotEmpty(sml.getDescricao())) {
				if (!mascLinha.toString().isEmpty()) {
					mascLinha.append('\n');
				}
				mascLinha.append(sml.getDescricao());
			}
			String str = obterDadosSumarioMascCampoEdit(sml.getPirAtdSeq(), sml.getPirDthrCriacao(), sml.getNroLinha(), sml.getOrdemRelatorio());
			if (StringUtils.isNotEmpty(str)) {
				if (!longMasc.toString().isEmpty()) {
					longMasc.append('\n');
				}
				mascLinha.append(str);
			}
		}
		vo.setDescricaoMascLinha(mascLinha.toString());
		vo.setDescricaolongMasc(longMasc.toString());
	}
	
	/**
	 * Q_MASC_CAMPO_EDIT
	 * @param atdSeq
	 * @param dthrCriacao
	 * @param nroLinha
	 * @param ordemRelatorio
	 * @return
	 */
	private String obterDadosSumarioMascCampoEdit(Integer atdSeq, Date dthrCriacao, Integer nroLinha, Integer ordemRelatorio) {
		String str = null;		
		MamPcSumMascCampoEdit edit = getPrescricaoMedicaFacade().obterSumarioMascCampoEdit(atdSeq, dthrCriacao, nroLinha, ordemRelatorio);
		if (edit != null) {
			str = CoreUtil.converterRTF2Text(edit.getDescricao());
		}
		return str;
	}
	
	/**
	 * Q_1
	 * @param vo
	 */
	private void preencherDadosDescricaoItemOrdem1(RelatorioParadaInternacaoVO vo) {
		vo.setDescricaoItemOrdem1(obterDadosDescricaoItem(vo, Boolean.FALSE));
	}
	
	/**
	 * Q_2
	 * @param vo
	 */
	private void preencherDadosDescricaoItemOrdem2(RelatorioParadaInternacaoVO vo) {
		vo.setDescricaoItemOrdem2(obterDadosDescricaoItem(vo, Boolean.TRUE));
	}

	private String obterDadosDescricaoItem(RelatorioParadaInternacaoVO vo, Boolean isOrdemMaiorIgual) {
		StringBuilder sb = new StringBuilder();
		List<MamPcIntItemParada> itemParadas = getPrescricaoMedicaFacade().pesquisarItemParadaPorAtendimento(vo.getAtdSeq(), vo.getDthrCriacao(), ORDEM, isOrdemMaiorIgual);
		
		for (MamPcIntItemParada item : itemParadas) {
			sb.append(item.getDescricao()).append("\n\n");
		}
		
		return StringUtils.removeEnd(sb.toString(), "\n\n");
	} 
	
	/**
	 * Q_SEM
	 * @param vo
	 */
	private void preencherDadosSumarioExamesMasc(RelatorioParadaInternacaoVO vo) {
		List<MamPcSumExameMasc> result = getPrescricaoMedicaFacade().pesquisarSumarioExamesMasc(vo.getAtdSeq(), vo.getDthrCriacao(), vo.getPacCodigo(), vo.getRecemNascido());
		for (MamPcSumExameMasc sem : result) {
			RelatorioParadaInternacaoDetalhesExamesVO detalheVO = new RelatorioParadaInternacaoDetalhesExamesVO();
			detalheVO.setDataEvento(sem.getDthrEventoLib());
			detalheVO.setOrdemRelatorio(sem.getOrdemRelatorio());
			detalheVO.setAtdSeq(vo.getAtdSeq());
			detalheVO.setDthrCriacao(vo.getDthrCriacao());
			AelExames exa = getExamesFacade().obterAelExamesPeloId(sem.getUfeEmaExaSigla());
			if (exa != null) {
				detalheVO.setExaDescricao(exa.getDescricao());
			}
			AelMateriaisAnalises man = getExamesFacade().obterMaterialAnalisePeloId(sem.getUfeEmaManSeq());
			if (man != null) {
				detalheVO.setManDescricao(man.getDescricao());
			}
			preencherDadosSumarioMascLinha(detalheVO); //Q_MASC_LINHA
			vo.getDetalhesExames().add(detalheVO);
		}
	}
	
	/**
	 * 
	 * @param atdSeq
	 * @param dthrCriacao
	 * @return
	 */
	public RelatorioParadaInternacaoVO montarRealatorio(Integer atdSeq, Date dthrCriacao) {
		RelatorioParadaInternacaoVO vo = new RelatorioParadaInternacaoVO();
		
		//Informa parametros de pesquisa
		vo.setAtdSeq(atdSeq);
		vo.setDthrCriacao(dthrCriacao);
		
		preencherDadosDescricaoItemOrdem1(vo); //Q_1
		preencherDadosSumarioExameTabela(vo);  //Q_SUE
		preencherDadosSumarioExameTabela2(vo); //Q_SUE2
		preencherDadosSumarioExamesMasc(vo);   //Q_SEM
		preencherDadosDescricaoItemOrdem2(vo); //Q_2
		preencherDadosRodape(vo);      		   // Campos Rodapé
		preencherDadosControle(vo);			   //Q_CONTROLE
		
		return vo;
	}

	/**
	 * Q_CONTROLE
	 * @param vo
	 */
	private void preencherDadosControle(RelatorioParadaInternacaoVO vo) {
		List<MamPcControlePac> controles = getPrescricaoMedicaFacade().pesquisarControlePaciente(vo.getAtdSeq(), vo.getDthrCriacao());
		for (MamPcControlePac  ctrl : controles) {
			if (validarDataHoraQControle(ctrl.getMamPcHorarioCtrl())) {
				RelatorioParadaInternacaoControleVO controleVO = new RelatorioParadaInternacaoControleVO();
				controleVO.setUnidade(ctrl.getUnidade());
				controleVO.setDescricaoControle(ctrl.getDescricaoControle());
				controleVO.setSigla(ctrl.getSigla());
				controleVO.setDataHora(ctrl.getMamPcHorarioCtrl().getDataHora());
				controleVO.setHrsControleSumario(ctrl.getMamPcHorarioCtrl().getHrsControleSumario());
				if(ctrl.getMamPcHorarioCtrl().getAnotacoes() != null){
					controleVO.setAnotacoes(" - ".concat(ctrl.getMamPcHorarioCtrl().getAnotacoes()));
				}
				vo.getControles().add(controleVO);
			}
		}
	}
	
	private Boolean validarDataHoraQControle(MamPcHorarioCtrl phc) {
		Boolean retorno = Boolean.FALSE;
		
		if (phc != null && phc.getHrsControleSumario() != null) {
			Date data = DateUtil.adicionaDias(DateUtil.obterDataComHoraInical(new Date()), (phc.getHrsControleSumario() / (-24)));
			retorno = DateUtil.validaDataMaior(phc.getDataHora(), data);
		}
		
		return retorno;
	}

	private void preencherDadosRodape(RelatorioParadaInternacaoVO vo) {
		StringBuilder sb = new StringBuilder();
		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(vo.getAtdSeq());
		Integer prontuario = atendimento.getPaciente().getProntuario();
		
		if (atendimento.getLeito() != null) {
			vo.setAgenda(atendimento.getLeito().getLeitoID());
		}
		
		vo.setNomePaciente(getAmbulatorioFacade().obterDescricaoCidCapitalizada(atendimento.getPaciente().getNome(), CapitalizeEnum.TODAS));
		vo.setProntuario(CoreUtil.formataProntuario(prontuario));
		
		if (prontuario != null && prontuario > VALOR_MAXIMO_PRONTUARIO) {
			if (atendimento.getGsoPacCodigo() != null) {
				AipPacientes aipPaciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(atendimento.getGsoPacCodigo());
				if (aipPaciente != null) {
					if (aipPaciente.getProntuario() != null && aipPaciente.getProntuario() > VALOR_MAXIMO_PRONTUARIO) {
						sb.append("MÃE:").append(CoreUtil.formataProntuario(aipPaciente.getProntuario()));
						vo.setProntuario(sb.toString());
					} else {
						sb.append(CoreUtil.formataProntuario(prontuario)).append("MÃE:").append(CoreUtil.formataProntuario(aipPaciente.getProntuario()));
						vo.setProntuario(sb.toString());
					}
				}
			}
		}
	}	
}

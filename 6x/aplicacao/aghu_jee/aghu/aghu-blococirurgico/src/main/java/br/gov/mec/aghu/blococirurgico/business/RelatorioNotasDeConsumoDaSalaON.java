package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoNotaSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoUtilCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialImpNotaSalaUnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialPorCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcUnidadeNotaSalaDAO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoLateralidadeProcCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioNotasDeConsumoDaSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaEquipamentosVO;
import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaMateriaisVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcUnidadeNotaSalaId;

@Stateless
public class RelatorioNotasDeConsumoDaSalaON extends BaseBusiness {

	private static final String HH_MM = "HH:mm";

	private static final Log LOG = LogFactory.getLog(RelatorioNotasDeConsumoDaSalaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	public MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	public MbcEquipamentoNotaSalaDAO mbcEquipamentoNotaSalaDAO;

	@Inject
	public MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	public MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	public MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	public MbcUnidadeNotaSalaDAO mbcUnidadeNotaSalaDAO;

	@Inject
	public MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	public MbcMaterialImpNotaSalaUnDAO mbcMaterialImpNotaSalaUnDAO;

	@Inject
	public MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;

	@Inject
	public MbcEquipamentoUtilCirgDAO mbcEquipamentoUtilCirgDAO;

	@Inject
	public MbcMaterialPorCirurgiaDAO mbcMaterialPorCirurgiaDAO;


	@EJB
	public EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	public IParametroFacade iParametroFacade;

	public static final long serialVersionUID = 2396491815090457486L;

	public List<RelatorioNotasDeConsumoDaSalaVO> listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(Short seqUnidadeCirurgica, Date dataCirurgia, 
			Short numeroAgenda, Boolean nsDigitada) throws ApplicationBusinessException {
		
		List<MbcCirurgias> listCirurgia = getMbcCirurgiasDAO()
			.listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(seqUnidadeCirurgica, dataCirurgia, numeroAgenda, nsDigitada);
		
		List<RelatorioNotasDeConsumoDaSalaVO> listVo = new ArrayList<RelatorioNotasDeConsumoDaSalaVO>();
		for (MbcCirurgias cirurgia : listCirurgia) {
			RelatorioNotasDeConsumoDaSalaVO vo = new RelatorioNotasDeConsumoDaSalaVO();
			vo.setSciSeqp(cirurgia.getSalaCirurgica().getId().getSeqp());
			vo.setPacProntuarioFormatado(CoreUtil.formataProntuario(cirurgia.getPaciente().getProntuario()));
			vo.setPacProntuario(formataProntuarioBarcode(cirurgia.getPaciente().getProntuario()));
			vo.setPacNome(cirurgia.getPaciente().getNome());
			vo.setQuarto(StringUtils.substring(getEscalaCirurgiasON().pesquisaQuarto(cirurgia.getPaciente().getCodigo()), 2));
			vo.setCnvCodigoDescricao(preencherCnvCodDescricao(cirurgia));
			vo.setNroAgenda(cirurgia.getNumeroAgenda());
			vo.setProcedimento(preencherListPciDescricao(cirurgia.getSeq()));
			vo.setDtHrInicioCirurgia(DateUtil.obterDataFormatada(cirurgia.getDataInicioCirurgia(), HH_MM));
			vo.setDtHrFimCirurgia(DateUtil.obterDataFormatada(cirurgia.getDataFimCirurgia(), HH_MM));
			vo.setDescricaoAnestesia(preencherAnestesiaDescricao(cirurgia.getSeq()));
			preencherEquipeCirurgiao(cirurgia.getSeq(), vo, DominioFuncaoProfissional.MPF);
			preencherEquipeAnestesista(cirurgia.getSeq(), vo, DominioFuncaoProfissional.ANP);
			preencherEquipeEnfermeira(cirurgia.getSeq(), vo, DominioFuncaoProfissional.ENF);
			preencherEquipeCirculante(cirurgia.getSeq(), vo, DominioFuncaoProfissional.CIR);
			preencherEquipeInstrument(cirurgia.getSeq(), vo, DominioFuncaoProfissional.INS);
			vo.setSubRelatorioExames(getMbcSolicitacaoEspExecCirgDAO().obterExamesPorCrgSeq(cirurgia.getSeq()));
			vo.setSubRelatorioSangueUtilizado(getMbcSolicHemoCirgAgendadaDAO().obterSangueUtilizadoPorCrgSeq(cirurgia.getSeq()));
			
			if (nsDigitada.equals(Boolean.TRUE)) {
				vo.setDtHrEntradaSala(DateUtil.obterDataFormatada(cirurgia.getDataEntradaSala(), HH_MM));
				vo.setDtHrSaidaSala(DateUtil.obterDataFormatada(cirurgia.getDataSaidaSala(), HH_MM));
				vo.setDtHrInicioAnest(DateUtil.obterDataFormatada(cirurgia.getDataInicioAnestesia(), HH_MM));
				vo.setDtHrFimAnest(DateUtil.obterDataFormatada(cirurgia.getDataFimAnestesia(), HH_MM));
				vo.setIndUtilO2(cirurgia.getUtilizaO2() ? "Sim" : "Não");
				vo.setIndPrc(cirurgia.getIndPrc() ? "Sim" : "Não");
				vo.setAtbProf(cirurgia.getAtbProf() ? "Sim" : "Não");
				vo.setDtHrAtbProf(DateUtil.obterDataFormatada(cirurgia.getDtHrAtbProf(), HH_MM));
				
				vo.setAplicaListaCirurgiaSegura(cirurgia.getAplicaListaCirurgiaSegura());
				if (cirurgia.getMotivoCancelamento() != null) {
					vo.setMotivoCancelamento(cirurgia.getMotivoCancelamento().getDescricao());
				}
				if (cirurgia.getDestinoPaciente() != null) {
					vo.setDestinoPaciente(cirurgia.getDestinoPaciente().getDescricao());
				}
				preencherListaEquipamentosUtilizados(cirurgia.getSeq(), vo);
				preencherListaOrteseProteseUtilizados(cirurgia.getSeq(), vo);
				preencherListaMateriaisUtilizados(cirurgia.getSeq(), vo);
				
			} else {
				preencherListaEquipamentos(seqUnidadeCirurgica, cirurgia.getSeq(), cirurgia.getEspecialidade().getSeq(), vo);
				preencherListaOrteseProtese(seqUnidadeCirurgica, cirurgia.getSeq(), cirurgia.getEspecialidade().getSeq(), vo);
				preencherListaMateriais(seqUnidadeCirurgica, cirurgia.getSeq(), cirurgia.getEspecialidade().getSeq(), vo);
			}
			
			listVo.add(vo);
		}
		
		CoreUtil.ordenarLista(listVo, RelatorioNotasDeConsumoDaSalaVO.Fields.DT_HR_INICIO.toString(), true);
		CoreUtil.ordenarLista(listVo, RelatorioNotasDeConsumoDaSalaVO.Fields.SCI_SEQP.toString(), true);
		
		return listVo;
	}
	
	public String preencherCnvCodDescricao(MbcCirurgias cirurgia) {
		String cnvCodigo = cirurgia.getConvenioSaude().getCodigo().toString();
		String cnvDescricao = cirurgia.getConvenioSaude().getDescricao();
		
		String retorno = StringUtils.leftPad(cnvCodigo, 3, "0") + " - " + cnvDescricao;
		return retorno;
	}
	
	public String preencherListPciDescricao(Integer crgSeq) {
		List<DescricaoLateralidadeProcCirurgicoVO> listDescricaoLateralidadeProcCirurgico = getMbcProcEspPorCirurgiasDAO().
		listarProcedimentosComLateralidadePorCrgSeq(crgSeq);
		String retorno = "";
		for (DescricaoLateralidadeProcCirurgicoVO procEspPorCirurgias : listDescricaoLateralidadeProcCirurgico) {
			if(retorno.isEmpty()){
				retorno = retorno.concat(preencherProcCodDescricaoLateralidade(procEspPorCirurgias));
			}else{
				retorno = retorno.concat("\n").concat(preencherProcCodDescricaoLateralidade(procEspPorCirurgias));
			}
		}
		return retorno;
	}

	public String preencherProcCodDescricaoLateralidade(DescricaoLateralidadeProcCirurgicoVO vo) {
		String procSeq = vo.getProcSeq().toString();
		String procDescricao = vo.getDescricaoProcedimento();
		String ladoCirurgia = this.obterLadoCirurgia(vo.getLadoCirurgia());
		
		String retorno = StringUtils.leftPad(procSeq, 5, "0") + " - " + procDescricao;
		if (!ladoCirurgia.isEmpty()) {
			retorno = retorno.concat(" - " + ladoCirurgia);
		}
		return retorno;
	}
	
	public String preencherAnestesiaDescricao(Integer crgSeq) {
		List<MbcAnestesiaCirurgias> listMbcAnestesiaCirurgias = getMbcAnestesiaCirurgiasDAO()
			.listarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(crgSeq);
		String retorno = "";
		for (MbcAnestesiaCirurgias anestesiaCirurgias : listMbcAnestesiaCirurgias) {
			if(retorno.isEmpty()){
				retorno = retorno.concat(anestesiaCirurgias.getMbcTipoAnestesias().getDescricao());
			}else{
				retorno = retorno.concat(", ").concat(anestesiaCirurgias.getMbcTipoAnestesias().getDescricao());
			}
		}
		return retorno;
	}
	
	public void preencherEquipeCirurgiao(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo, DominioFuncaoProfissional tipoEquipe) {
		List<Object[]> cirurgiao = this.getMbcProfCirurgiasDAO()
			.obterEquipePorCrgSeqAgrupado(crgSeq, tipoEquipe);
		
		popularVoEquipeCirurgiaoAnestesista(cirurgiao, vo, tipoEquipe);
	}
	
	public void preencherEquipeAnestesista(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo, DominioFuncaoProfissional tipoEquipe) {
		List<Object[]> anestesista = this.getMbcProfCirurgiasDAO()
			.obterEquipePorCrgSeqAgrupado(crgSeq, tipoEquipe);
		
		popularVoEquipeCirurgiaoAnestesista(anestesista, vo, tipoEquipe);
	}
	
	public void preencherEquipeEnfermeira(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo, DominioFuncaoProfissional tipoEquipe) {
		List<Object[]> enfermeira = this.getMbcProfCirurgiasDAO()
			.obterEquipePorCrgSeqAgrupado(crgSeq, tipoEquipe);
		
		popularVoEquipeEnfCirIns(enfermeira, vo, tipoEquipe);
	}
	
	public void preencherEquipeCirculante(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo, DominioFuncaoProfissional tipoEquipe) {
		List<Object[]> circulante = this.getMbcProfCirurgiasDAO()
			.obterEquipePorCrgSeqAgrupado(crgSeq, tipoEquipe);
		
		popularVoEquipeEnfCirIns(circulante, vo, tipoEquipe);
	}
	
	public void preencherEquipeInstrument(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo, DominioFuncaoProfissional tipoEquipe) {
		List<Object[]> instrument = this.getMbcProfCirurgiasDAO()
			.obterEquipePorCrgSeqAgrupado(crgSeq, tipoEquipe);
		
		popularVoEquipeEnfCirIns(instrument, vo, tipoEquipe);
	}
	
	public void popularVoEquipeCirurgiaoAnestesista(List<Object[]> equipe, RelatorioNotasDeConsumoDaSalaVO vo, DominioFuncaoProfissional tipoEquipe) {
		for (Object[] obj : equipe) {
			if ((String) obj[0] != null && tipoEquipe.equals(DominioFuncaoProfissional.MPF)) {
				vo.setCirurgiaoMpf((String) obj[0]);
				
			} else if ((String) obj[0] != null && tipoEquipe.equals(DominioFuncaoProfissional.ANP)) {
				vo.setAnestesistaAnp((String) obj[0]);
			}
			if ((String) obj[1] != null && tipoEquipe.equals(DominioFuncaoProfissional.MPF)) {
				vo.setCirurgiaoMco((String) obj[1]);
				
			} else if ((String) obj[1] != null && tipoEquipe.equals(DominioFuncaoProfissional.ANP)) {
				vo.setAnestesistaAnc((String) obj[1]);
			}
			if ((String) obj[2] != null && tipoEquipe.equals(DominioFuncaoProfissional.MPF)) {
				vo.setCirurgiaoMax((String) obj[2]);
				
			} else if ((String) obj[2] != null && tipoEquipe.equals(DominioFuncaoProfissional.ANP)) {
				vo.setAnestesistaAnr((String) obj[2]);
			}
		}
	}
	
	public void popularVoEquipeEnfCirIns(List<Object[]> equipe, RelatorioNotasDeConsumoDaSalaVO vo, DominioFuncaoProfissional tipoEquipe) {
		int rowNum = 0;
		for (Object[] obj : equipe) {
			rowNum = rowNum + 1;
			String vinCodigo = (((Short) obj[0]).toString());
			String matricula = (((Integer) obj[1]).toString());
			String nomeFormatado = "";

			if ((String) obj[2] != null) {
				nomeFormatado = ((String) obj[2]);
			} else {
				nomeFormatado = (((String) obj[3]).substring(0, 15));
			}
			String retorno = StringUtils.leftPad(vinCodigo, 3, "0")
					+ StringUtils.leftPad(matricula, 7, "0") + "-"
					+ nomeFormatado;

			if (tipoEquipe.equals(DominioFuncaoProfissional.ENF)) {
				switch (rowNum) {
				case 1:
					vo.setEnfermeiraEpf(retorno);
					break;

				case 2:
					vo.setEnfermeiraEco(retorno);
					break;

				case 3:
					vo.setEnfermeiraEax(retorno);
					break;
				}
			} else if (tipoEquipe.equals(DominioFuncaoProfissional.CIR)) {
				switch (rowNum) {
				case 1:
					vo.setCirculanteCpf(retorno);
					break;

				case 2:
					vo.setCirculanteCco(retorno);
					break;

				case 3:
					vo.setCirculanteCax(retorno);
					break;
				}
			} else if (tipoEquipe.equals(DominioFuncaoProfissional.INS)) {
				switch (rowNum) {
				case 1:
					vo.setInstrumentInp(retorno);
					break;

				case 2:
					vo.setInstrumentIno(retorno);
					break;

				case 3:
					vo.setInstrumentInx(retorno);
					break;
				}
			}
		}
	}
	
	public void preencherListaEquipamentos(Short seqUnidadeCirurgica, Integer crgSeq, Short crgEspSeq, RelatorioNotasDeConsumoDaSalaVO vo) {
		Integer pciSeq = obterPciSeq(seqUnidadeCirurgica, crgSeq);
		Short espSeq = obterEspSeq(seqUnidadeCirurgica, crgEspSeq, pciSeq);
		
		List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> retorno = 
			getMbcEquipamentoNotaSalaDAO().obterEquipamentosPorUnfSeqCrgSeq(seqUnidadeCirurgica, crgSeq, pciSeq, espSeq);
		
		int rowNum = 0;
		String coluna1 = "";
		String coluna2 = "";
		String coluna3 = "";
		String coluna4 = "";
		for (SubRelatorioNotasDeConsumoDaSalaEquipamentosVO equipamento : retorno) {
			rowNum = rowNum + 1;
			if (rowNum <= 7) {
				if(coluna1.isEmpty()) {
					coluna1 = coluna1.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna1 = coluna1.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
				
			} else if (rowNum > 7 && rowNum <= 14) {
				if(coluna2.isEmpty()) {
					coluna2 = coluna2.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna2 = coluna2.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
				
			} else if (rowNum > 14 && rowNum <= 21) {
				if(coluna3.isEmpty()) {
					coluna3 = coluna3.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna3 = coluna3.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
				
			} else if (rowNum > 21) {
				if(coluna4.isEmpty()) {
					coluna4 = coluna4.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna4 = coluna4.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
			}
		}
		vo.setDescricaoEquipamentoColuna1(coluna1);
		vo.setDescricaoEquipamentoColuna2(coluna2);
		vo.setDescricaoEquipamentoColuna3(coluna3);
		vo.setDescricaoEquipamentoColuna4(coluna4);
	}
	
	public void preencherListaEquipamentosUtilizados(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo) {
		List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> retorno = getMbcEquipamentoUtilCirgDAO().obterEquipamentosUtilizadosPorCrgSeq(crgSeq);
		
		int rowNum = 0;
		String coluna1 = "";
		String coluna2 = "";
		String coluna3 = "";
		String coluna4 = "";
		for (SubRelatorioNotasDeConsumoDaSalaEquipamentosVO equipamento : retorno) {
			rowNum = rowNum + 1;
			if (rowNum <= 7) {
				if(coluna1.isEmpty()) {
					coluna1 = coluna1.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna1 = coluna1.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
				
			} else if (rowNum > 7 && rowNum <= 14) {
				if(coluna2.isEmpty()) {
					coluna2 = coluna2.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna2 = coluna2.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
				
			} else if (rowNum > 14 && rowNum <= 21) {
				if(coluna3.isEmpty()) {
					coluna3 = coluna3.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna3 = coluna3.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
				
			} else if (rowNum > 21) {
				if(coluna4.isEmpty()) {
					coluna4 = coluna4.concat(equipamento.getDescricaoEquipamento());
				}else{
					coluna4 = coluna4.concat("\n").concat(equipamento.getDescricaoEquipamento());
				}
			}
		}
		vo.setDescricaoEquipamentoColuna1(coluna1);
		vo.setDescricaoEquipamentoColuna2(coluna2);
		vo.setDescricaoEquipamentoColuna3(coluna3);
		vo.setDescricaoEquipamentoColuna4(coluna4);
	}
	
	public void preencherListaOrteseProtese(Short seqUnidadeCirurgica, Integer crgSeq, Short crgEspSeq, RelatorioNotasDeConsumoDaSalaVO vo) throws ApplicationBusinessException {
		Integer pciSeq = obterPciSeq(seqUnidadeCirurgica, crgSeq);
		Short espSeq = obterEspSeq(seqUnidadeCirurgica, crgEspSeq, pciSeq);
		
		AghParametros parametroGrupo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
		Integer grupoMatOrtProt = parametroGrupo.getVlrNumerico().intValue();
		
		List<String> retorno = getMbcMaterialImpNotaSalaUnDAO().obterListaOrteseProtesePorUnfSeqCrgSeq(seqUnidadeCirurgica, crgSeq, pciSeq, espSeq, grupoMatOrtProt);
		
		int rowNum = 0;
		String coluna1 = "";
		
		for (String orteseProtese : retorno) {
			if(orteseProtese == null){
				++rowNum;
				continue;
			}
			rowNum = rowNum + 1;
			if(coluna1.isEmpty()) {
				coluna1 = coluna1.concat(orteseProtese);
			}else{
				coluna1 = coluna1.concat("\n").concat(orteseProtese);
			}
				
		}
		vo.setDescricaoOrteseProteseColuna1(coluna1);
	}
	
	public void preencherListaOrteseProteseUtilizados(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo) throws ApplicationBusinessException {
		
		List<String> retorno = 
			getMbcMaterialPorCirurgiaDAO().obterListaOrteseProteseUtilizadosPorCrgSeq(crgSeq);
		
		int rowNum = 0;
		String coluna1 = "";
		String coluna2 = "";
		String coluna3 = "";
		
		for (String orteseProtese : retorno) {
			rowNum = rowNum + 1;
			if (rowNum <= 9) {
				if(coluna1.isEmpty()) {
					coluna1 = coluna1.concat(orteseProtese);
				}else{
					coluna1 = coluna1.concat("\n").concat(orteseProtese);
				}
				
			} else if (rowNum > 9 && rowNum <= 18) {
				if(coluna2.isEmpty()) {
					coluna2 = coluna2.concat(orteseProtese);
				}else{
					coluna2 = coluna2.concat("\n").concat(orteseProtese);
				}
				
			} else if (rowNum > 18) {
				if(coluna3.isEmpty()) {
					coluna3 = coluna3.concat(orteseProtese);
				}else{
					coluna3 = coluna3.concat("\n").concat(orteseProtese);
				}
			}
		}
		vo.setDescricaoOrteseProteseColuna1(coluna1);
		vo.setDescricaoOrteseProteseColuna2(coluna2);
		vo.setDescricaoOrteseProteseColuna3(coluna3);
	}
	
	public void preencherListaMateriais(Short seqUnidadeCirurgica, Integer crgSeq, Short crgEspSeq, RelatorioNotasDeConsumoDaSalaVO vo) throws ApplicationBusinessException {
		Integer pciSeq = obterPciSeq(seqUnidadeCirurgica, crgSeq);
		Short espSeq = obterEspSeq(seqUnidadeCirurgica, crgEspSeq, pciSeq);
		
		MbcUnidadeNotaSalaId unidadeNotaSalaId = getMbcUnidadeNotaSalaDAO().obterUnidadeNotaSalaIdPorPciSeqEspSeq(seqUnidadeCirurgica, pciSeq, espSeq);
		// Se unidadeNotaSalaId for null, provavelmente ela está inativa.
		if (unidadeNotaSalaId != null) {
			Short notaSalaSeqp = unidadeNotaSalaId.getSeqp();
			Short notaSalaUnfSeq = unidadeNotaSalaId.getUnfSeq();
			
			AghParametros parametroGrupo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
			Integer grupoMatOrtProt = parametroGrupo.getVlrNumerico().intValue();
			
			List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> retorno =
				getMbcMaterialImpNotaSalaUnDAO().obterListaMateriaisPorSeqpUnfSeq(notaSalaSeqp, notaSalaUnfSeq, grupoMatOrtProt);
			
			adicionaLinhasJustificativa(retorno);
			
			vo.setSubRelatorioMateriais(retorno);
		}
	}
	
	public void preencherListaMateriaisUtilizados(Integer crgSeq, RelatorioNotasDeConsumoDaSalaVO vo) throws ApplicationBusinessException {
		AghParametros parametroGrupo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
		Integer grupoMatOrtProt = parametroGrupo.getVlrNumerico().intValue();
		
		List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> retorno =
			getMbcMaterialPorCirurgiaDAO().obterListaMateriaisUtilizadosPorCrgSeq(crgSeq, grupoMatOrtProt);
		
		adicionaLinhasJustificativa(retorno);
		
		vo.setSubRelatorioMateriais(retorno);
	}

	public void adicionaLinhasJustificativa(List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> retorno) {
		// Adiciona as linhas de justificativa para uso de material extra
		String linhaEmBranco = "____________________________________________";
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linha1 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linha1.setDescricaoMaterial("JUSTIFICATIVA PARA USO DE MATERIAL EXTRA");
		linha1.setOrdem((short) 989);
		retorno.add(linha1);
		
		// Adiciona objetos sem descrição para que a justificativa fique alinhada no relatório
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linhaVazia1 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linhaVazia1.setDescricaoMaterial("");
		retorno.add(linhaVazia1);
		
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linha2 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linha2.setDescricaoMaterial(linhaEmBranco);
		linha2.setOrdem((short) 991);
		retorno.add(linha2);
		
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linhaVazia2 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linhaVazia2.setDescricaoMaterial("");
		retorno.add(linhaVazia2);
		
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linha3 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linha3.setDescricaoMaterial(linhaEmBranco);
		linha3.setOrdem((short) 992);
		retorno.add(linha3);
		
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linhaVazia3 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linhaVazia3.setDescricaoMaterial("");
		retorno.add(linhaVazia3);
		
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linha4 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linha4.setDescricaoMaterial(linhaEmBranco);
		linha4.setOrdem((short) 993);
		retorno.add(linha4);
		
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linhaVazia4 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linhaVazia4.setDescricaoMaterial("");
		retorno.add(linhaVazia4);
		
		SubRelatorioNotasDeConsumoDaSalaMateriaisVO linha5 = new SubRelatorioNotasDeConsumoDaSalaMateriaisVO();
		linha5.setDescricaoMaterial(linhaEmBranco);
		linha5.setOrdem((short) 994);
		retorno.add(linha5);
	}
	
	/**
	 * @ORADB function CF_PCIFORMULA - retorna o parâmetro PCI_SEQ
	 * 
	 * @param seqUnidadeCirurgica
	 * @param crgSeq
	 * @return
	 */
	public Integer obterPciSeq(Short seqUnidadeCirurgica, Integer crgSeq) {
		Integer eprPciSeq = getMbcProcEspPorCirurgiasDAO().obterMbcProcEspPorCirurgiaPorCrgSeq(crgSeq);
		if (eprPciSeq == null) {
			eprPciSeq = 999999;
		}
		Integer pciSeq = getMbcUnidadeNotaSalaDAO().obterNotaSalaPorUnfSeqPciSeq(seqUnidadeCirurgica, eprPciSeq);
		if (pciSeq != null) {
			return eprPciSeq;
		}
		return 999999;
	}
	
	/**
	 * @ORADB function CF_ESPFormula - retorna o parâmetro ESP_SEQ
	 * 
	 * @param seqUnidadeCirurgica
	 * @param espSeq
	 * @param pciSeq
	 * @return
	 */
	public Short obterEspSeq(Short seqUnidadeCirurgica, Short espSeq, Integer pciSeq) {
		if (pciSeq != 999999) {
			return espSeq;
		}
		Short retorno = getMbcUnidadeNotaSalaDAO().obterNotaSalaPorUnfSeqEspSeq(seqUnidadeCirurgica, espSeq);
		if(retorno != null) {
			return espSeq;
		}
		return (short) 999999;
	}
	
	/**
	 * Obtém o lado da cirurgia
	 * 
	 * @param ladoCirurgiaAgenda
	 * @return
	 */
	public String obterLadoCirurgia(DominioLadoCirurgiaAgendas ladoCirurgiaAgenda) {
		if (ladoCirurgiaAgenda != null) {
			switch (ladoCirurgiaAgenda) {
			case D:
				return "Lado Direito";
			case E:
				return "Lado Esquerdo";
			case B:
				return "Bilateral";
			default:
				break;
			}
		}
		return "";
	}
	
	/**
	 * Formata o prontuário para ler no scanner de código de barras
	 * 
	 * @return
	 */
	public String formataProntuarioBarcode(Integer prontuarioPaciente){
		if (prontuarioPaciente == null) {
			return "";
		}
		String prontFormatado = StringUtils.leftPad(String.valueOf(prontuarioPaciente), 9, '0');
		prontFormatado = StringUtils.rightPad(prontFormatado, 12, '0');
		return prontFormatado;
	}
	
	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return mbcSolicHemoCirgAgendadaDAO;
	}
	
	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
	
	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgDAO() {
		return mbcSolicitacaoEspExecCirgDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	
	protected MbcUnidadeNotaSalaDAO getMbcUnidadeNotaSalaDAO() {
		return mbcUnidadeNotaSalaDAO;
	}
	
	protected MbcEquipamentoNotaSalaDAO getMbcEquipamentoNotaSalaDAO() {
		return mbcEquipamentoNotaSalaDAO;
	}
	
	protected MbcEquipamentoUtilCirgDAO getMbcEquipamentoUtilCirgDAO() {
		return mbcEquipamentoUtilCirgDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected MbcMaterialImpNotaSalaUnDAO getMbcMaterialImpNotaSalaUnDAO() {
		return mbcMaterialImpNotaSalaUnDAO;
	}
	
	protected MbcMaterialPorCirurgiaDAO getMbcMaterialPorCirurgiaDAO() {
		return mbcMaterialPorCirurgiaDAO;
	}
}

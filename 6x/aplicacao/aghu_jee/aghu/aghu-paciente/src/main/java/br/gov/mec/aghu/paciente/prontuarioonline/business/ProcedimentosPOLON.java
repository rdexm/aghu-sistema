package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.PdtImg;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosImagemPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ProcedimentosPOLON extends BaseBusiness {
	private static final long serialVersionUID = -6923127774871541311L;

	
	@EJB
	private ProcedimentosPOLRN procedimentosPOLRN;
	
	private static final Log LOG = LogFactory.getLog(ProcedimentosPOLON.class);
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	
	
	public List<ProcedimentosPOLVO> pesquisarProcedimentosPOL(Integer codigo) throws ApplicationBusinessException {

		Set<ProcedimentosPOLVO> setProcedimentos = new HashSet<ProcedimentosPOLVO>();

		/**
		 * concatencao das Listas
		 */
		setProcedimentos.addAll(pesquisarProcedimentosComDescricaoPOL(codigo));
		setProcedimentos.addAll(pesquisarProcedimentosSemDescricaoPOL(codigo));

		setProcedimentos.addAll(pesquisarExames(codigo));

		setProcedimentos.addAll(pesquisarProcedimentosPortal(codigo));

		if (setProcedimentos == null || setProcedimentos.isEmpty()) {

			return null;

		} else {

			List<ProcedimentosPOLVO> listaProcedimentos = new ArrayList<ProcedimentosPOLVO>(setProcedimentos);

			if (!(listaProcedimentos.isEmpty())) {

				// remove row nula da VO
				while (listaProcedimentos.remove(null)) {
					listaProcedimentos.remove(null);
				}
			}

			CoreUtil.ordenarLista(listaProcedimentos, "data", false);

			for(ProcedimentosPOLVO vo : listaProcedimentos){
				vo.setId(listaProcedimentos.indexOf(vo));
			}
			return listaProcedimentos;
		}
	}
	
	public List<ProcedimentosPOLVO> pesquisarProcedimentosComDescricaoPOL(Integer pacCodigo) throws ApplicationBusinessException{
		List<ProcedimentosPOLVO> listaProcedimentos = new ArrayList<ProcedimentosPOLVO>();	

		listaProcedimentos.addAll(blocoCirurgicoFacade.pesquisarProcedimentosComDescricaoPOL(pacCodigo));	
		listaProcedimentos.addAll(blocoCirurgicoProcDiagTerapFacade.pesquisarProcedimentosPDTComDescricaoPOL(pacCodigo));	

		for (ProcedimentosPOLVO procedimentosVO : listaProcedimentos) {
			String pacOruAccNummer = blocoCirurgicoFacade.obterPacOruAccNummer(procedimentosVO.getSeq());
			procedimentosVO.setPacOruAccNummer(pacOruAccNummer);
			procedimentosVO.setTipo("C");		

			if (procedimentosVO.getSituacao() != null && procedimentosVO.getSituacao() == DominioSituacaoCirurgia.CANC) {
				procedimentosVO.setHabilitaBotaoMotivoCancel(Boolean.TRUE);
			} else {
				procedimentosVO.setHabilitaBotaoMotivoCancel(Boolean.FALSE);
			}
		}	

		return listaProcedimentos;
	}
	
	public List<ProcedimentosPOLVO> pesquisarProcedimentosSemDescricaoPOL(Integer pacCodigo) throws ApplicationBusinessException {
		
		List<ProcedimentosPOLVO> listaProcedimentos = getBlocoCirurgicoFacade().pesquisarProcedimentosSemDescricaoPOL(pacCodigo);	

		for (int i = 0; i < listaProcedimentos.size(); i++) {		

			ProcedimentosPOLVO procedimentosVO = listaProcedimentos.get(i);		

			if( (!Boolean.TRUE.equals(procedimentosVO.getTemDescricao()) && Boolean.TRUE.equals(procedimentosVO.getDigitaNotaSala()) && 
						!DominioIndRespProc.NOTA.equals(procedimentosVO.getIndRespProc())
				) ||
				(!Boolean.TRUE.equals(procedimentosVO.getTemDescricao()) && !Boolean.TRUE.equals(procedimentosVO.getDigitaNotaSala()) && 
							!DominioIndRespProc.AGND.equals(procedimentosVO.getIndRespProc())
				)
			   ) {
				// assinala row como nula para remoção
				listaProcedimentos.set(i, null);
			} else {
				String pacOruAccNummer = blocoCirurgicoFacade.obterPacOruAccNummer(procedimentosVO.getSeq());
				procedimentosVO.setPacOruAccNummer(pacOruAccNummer);
				procedimentosVO.setTipo("C");			

				if (procedimentosVO.getSituacao() != null && procedimentosVO.getSituacao() == DominioSituacaoCirurgia.CANC) {
					procedimentosVO.setHabilitaBotaoMotivoCancel(Boolean.TRUE);
				} else {
					procedimentosVO.setHabilitaBotaoMotivoCancel(Boolean.FALSE);
				}
			}
		}
		
		return listaProcedimentos;
	}

	// Procedimentos
	public List<ProcedimentosPOLVO> pesquisarProcedimentos(Integer codigo)
			throws ApplicationBusinessException {

		List<ProcedimentosPOLVO> listaProcedimentos = blocoCirurgicoFacade.pesquisarProcedimentosPOL(codigo);

		// for (CirurgiasInternacaoPOLVO cirurgiaVO: listaCirurgias){
		for (int i = 0; i < listaProcedimentos.size(); i++) {

			ProcedimentosPOLVO procedimentosVO = listaProcedimentos.get(i);

			// Testa condição para seleção da Cirurgia
			if( (Boolean.TRUE.equals(procedimentosVO.getTemDescricao()) &&  !DominioIndRespProc.DESC.equals(procedimentosVO.getIndRespProc())) ||
				(!Boolean.TRUE.equals(procedimentosVO.getTemDescricao()) && Boolean.TRUE.equals(procedimentosVO.getDigitaNotaSala()) && 
							!DominioIndRespProc.NOTA.equals(procedimentosVO.getIndRespProc())
				) ||
				(!Boolean.TRUE.equals(procedimentosVO.getTemDescricao()) && !Boolean.TRUE.equals(procedimentosVO.getDigitaNotaSala()) && 
							!DominioIndRespProc.AGND.equals(procedimentosVO.getIndRespProc())
				)
  		     ){

				// assinala row como nula para remoção
				listaProcedimentos.set(i, null);

			} else {
				String pacOruAccNummer = blocoCirurgicoFacade.obterPacOruAccNummer(procedimentosVO.getSeq());

				procedimentosVO.setPacOruAccNummer(pacOruAccNummer);
				procedimentosVO.setTipo("C");

				// Habilita botão Motivo Cancelamento se Situacao = Cirurgia/PDT Cancelada

				if (procedimentosVO.getSituacao() != null && procedimentosVO.getSituacao() == DominioSituacaoCirurgia.CANC) {
					procedimentosVO.setHabilitaBotaoMotivoCancel(Boolean.TRUE);
				} else {
					procedimentosVO.setHabilitaBotaoMotivoCancel(Boolean.FALSE);
				}
			}

		}
		return listaProcedimentos;
	}

	// Exames realizados como Procedimentos

	public List<ProcedimentosPOLVO> pesquisarExames(Integer codigo) throws ApplicationBusinessException {

		List<ProcedimentosPOLVO> listaExamesProc = getExamesFacade().pesquisarExamesProcPOL(codigo);

		// Busca parametro P_SITUACAO_LIBERADO
		String pSitLib = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		
		// Busca parametro P_SITUACAO_NA_AREA_EXECUTORA
		String pSitAreaExec = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		
		for (ProcedimentosPOLVO examesProcVO : listaExamesProc) {

			// Seta SEQ da cirurgia = 0 SEQ do procedimento = 0
			Integer valor = Integer.valueOf("0");
			examesProcVO.setSeq(valor);
			examesProcVO.setEprPciSeq(valor);

			// Seta Situacao = "RZDA" e Desabilita botão Motivo Cancelamento
			examesProcVO.setSituacao(DominioSituacaoCirurgia.RZDA);
			examesProcVO.setHabilitaBotaoMotivoCancel(Boolean.FALSE);
			examesProcVO.setTipo("E");
			examesProcVO.setData(getExamesFacade().obterDataExame(examesProcVO.getSoeSeq(), examesProcVO.getSoeSeqP(), pSitLib, pSitAreaExec));
		}

		return listaExamesProc;

	}

	// Procedimentos Agendados so no Portal - ainda nao esta em escala

	public List<ProcedimentosPOLVO> pesquisarProcedimentosPortal(Integer codigo) throws ApplicationBusinessException {

		List<ProcedimentosPOLVO> listaProcedimentosPortal = getBlocoCirurgicoFacade().pesquisarProcedimentosPortalPOL(codigo);

		// for (ProcedimentosPOLVO procPortalVO: listaProcedimentosPortal){
		for (int i = 0; i < listaProcedimentosPortal.size(); i++) {

			ProcedimentosPOLVO procPortalVO = listaProcedimentosPortal.get(i);

			Boolean cirPortal = getProcedimentosPOLRN().verificarSeEscalaPortalAgendamentoTemCirurgia(procPortalVO.getAgdSeq(), procPortalVO.getData());

			if (cirPortal == false) {

				// Seta SEQ da cirurgia = 0
				Integer valor = Integer.valueOf("0");
				procPortalVO.setSeq(valor);

				// Seta Situacao = "AGND" e Desabilita botão Motivo Cancelamento
				procPortalVO.setSituacao(DominioSituacaoCirurgia.AGND);
				procPortalVO.setHabilitaBotaoMotivoCancel(Boolean.FALSE);
				procPortalVO.setTipo("C");
				procPortalVO.setPacOruAccNummer(null);

			} else {

				// assinala row como nula para remoção
				listaProcedimentosPortal.set(i, null);
			}
		}

		if (!(listaProcedimentosPortal.isEmpty())) {

			// remove row nula da VO
			while (listaProcedimentosPortal.remove(null)) {
				listaProcedimentosPortal.remove(null);
			}
		}
		return listaProcedimentosPortal;
	}

	/**
	 * RN0 - habilitar botão: botaoDescricao
	 */
	public Boolean habilitarBotaoDescricao(ProcedimentosPOLVO procedimento) {
		Boolean botaoDescricao = false;

		if (procedimento.getSeq() == null || procedimento.getSeq() == 0) {
			botaoDescricao = false; // DESABILITAR OS BOTOES “DESCRICAO”,
									// “IMAGEM” E “ATO ANESTESICO”
		} else {
			if (getProcedimentosPOLRN().verDescCirurgica(procedimento.getSeq()) == null) {// RN12
				botaoDescricao = false;
			} else {
				botaoDescricao = true;
			}
		}

		if (verificarPRNTOLADMIN()) {
			botaoDescricao = false;
		}
		return botaoDescricao;
	}

	/**
	 * RN0 - habilitar botão: botaoImagem
	 * parametro MBCC_VERIFICA_IMAGEM_DDT_SEQ
	 */
	public Boolean habilitarBotaoImagem(ProcedimentosPOLVO procedimento, Integer parametroVerificaImagem)  {
		Boolean botaoImagem = false;

		if (procedimento.getSeq() != null && procedimento.getSeq() != 0) {
			if (getProcedimentosPOLRN().verDescCirurgica(procedimento.getSeq())) {// RN12
				if (procedimento.getPacOruAccNummer() != null) {
					botaoImagem = true;
				} else {
					if (Boolean.TRUE.equals(getProcedimentosPOLRN().verificarSeTemImagem(parametroVerificaImagem))) { // RN9
						botaoImagem = true;
					} else {
						botaoImagem = false;
					}
				}
			} else {
				if (procedimento.getPacOruAccNummer() != null) {
					botaoImagem = true;
				} else {
					botaoImagem = false;
				}
			}
		}

		if (verificarPRNTOLADMIN()) {
			botaoImagem = false;
		}
		return botaoImagem;
	}

	/**
	 * RN0 - habilitar botão: botaoExamesAnatomopatologicos
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Boolean habilitarBotaoExamesAnatomopatologicos(
			ProcedimentosPOLVO procedimento) throws ApplicationBusinessException {
		Boolean botaoExamesAnatomopatologicos = false;

//		AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		List<AelItemSolicitacaoExames> itemSolicitacaoExames = getExamesFacade()
				.pesquisarSolicitacaoExames(procedimento.getSeq(),
						retornarCodigosExameSituacao());

		if (itemSolicitacaoExames != null && !itemSolicitacaoExames.isEmpty()) {
			botaoExamesAnatomopatologicos = true;
		}
		if (verificarPRNTOLADMIN()) {
			botaoExamesAnatomopatologicos = false;
		}
		return botaoExamesAnatomopatologicos;
	}

	public Boolean verificarPRNTOLADMIN() {
		Boolean retorno = false;
		return retorno;
		//TODO ESCHWEIGERT 15/01/2015 REVER, em nenhum ponto na 5 esta variavel AIPF_PRNTOL_ADMIN é setada para TRUE???? 
		// variável global
//		Boolean prntolAdmin = (Boolean) obterContextoSessao("AIPF_PRNTOL_ADMIN");
//
//		if (prntolAdmin == null) {
////			atribuirContextoSessao(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN, Boolean.FALSE);// prntol_admin = "N"
//			prntolAdmin = Boolean.FALSE;
//		}
//		
//		if (prntolAdmin == true) {
//			retorno = true;
//		}
//		
//		return retorno;
	}

	public String[] retornarCodigosExameSituacao() throws ApplicationBusinessException {
		String[] retorno = {parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_LIBERADO),
							parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_EXECUTANDO)
						   };
		return retorno;
	}

	private ProcedimentosPOLRN getProcedimentosPOLRN() {
		return procedimentosPOLRN;
	}

	private IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	private IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	private IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return this.blocoCirurgicoProcDiagTerapFacade;
	}
	
	/**
	 * 16639
	 */
	public boolean verificaSeExisteImagem(Integer parametroVerificaImagem){
		
		boolean temImagem = false;
		if (Boolean.TRUE.equals(getProcedimentosPOLRN().verificarSeTemImagem(parametroVerificaImagem))) { 
			temImagem = true;
		} else {
			temImagem = false;
		}
		
		return temImagem;
	}
	
	public List<ProcedimentosImagemPOLVO> montarListaImagens(Integer seq) {
		
		List<PdtImg> lista = getBlocoCirurgicoProcDiagTerapFacade().pesquisarImagens(seq); 
		List<ProcedimentosImagemPOLVO> imagemPOLVO = new ArrayList<ProcedimentosImagemPOLVO>();
		
		for(PdtImg img : lista) {
			ProcedimentosImagemPOLVO imgVO = new ProcedimentosImagemPOLVO();
			
			imgVO.setImagem(img.getImagem());
			imgVO.setDescricaoImagem(img.getPdtDadoImg().getTexto());
			imgVO.setNomeImagem("image"+img.getId().getDdtSeq()+"_"+img.getId().getSeqp());
			
			imagemPOLVO.add(imgVO);
		}
		
		return imagemPOLVO;
	}
}

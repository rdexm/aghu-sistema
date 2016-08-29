package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioCoresSituacaoItemPrescrito;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdtoImagens;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;

@Stateless
public class RealizarTriagemMedicamentosPrescricaoON extends BaseBusiness implements Serializable {


@EJB
private RealizarTriagemMedicamentosPrescricaoRN realizarTriagemMedicamentosPrescricaoRN;

private static final Log LOG = LogFactory.getLog(RealizarTriagemMedicamentosPrescricaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@EJB
private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;

@EJB
private IEstoqueFacade estoqueFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IFarmaciaFacade farmaciaFacade;

@EJB
private IAdministracaoFacade administracaoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4833756518646235836L;

	public enum RealizarTriagemMedicamentosPrescricaoONExceptionCode implements	BusinessExceptionCode {
		REALIZAR_TRIAGEM_FARMACIA_OBRIGATORIA,
		NENHUM_VALOR_ENCONTRADO_FARMACIA, 
		NENHUM_VALOR_ENCONTRADO_OCORRENCIA, NENHUM_VALOR_ENCONTRADO_MOTIVO,
		SELECAO_FARMACIA_OBRIGATORIA_REALIZAR_TRIAGEM
	}
	
	public List<AfaDispensacaoMdtos> recuperarListaTriagemMedicamentosPrescricao(
			MpmPrescricaoMedica prescricaoMedica) {
		List<AfaDispensacaoMdtos> listaTriagem = getAfaDispensacaoMdtosDAO()
				.pesquisarDispensacaoMdtosPorPrescricaoESituacao(prescricaoMedica,
						DominioSituacaoDispensacaoMdto.S,
						DominioSituacaoDispensacaoMdto.T,
						DominioSituacaoDispensacaoMdto.D,
						DominioSituacaoDispensacaoMdto.C,
						DominioSituacaoDispensacaoMdto.E);
		
		for(AfaDispensacaoMdtos dispMdto:listaTriagem){
			dispMdto.setDescricaoMedicamentoPrescrito(getRealizarTriagemMedicamentosPrescricaoRN()
					.obterDescricaoMedicamentoPrescrito(dispMdto));
			dispMdto.setCorSituacaoItemPrescrito(obterCorSituacaoItemPrescrito(dispMdto));
			
			if(dispMdto.getUnidadeFuncional()!=null) {
				dispMdto.setSeqUnidadeFuncionalSelecionada(dispMdto.getUnidadeFuncional().getSeq().toString());
			}
			if(dispMdto.getTipoOcorrenciaDispensacao()!=null) {
				dispMdto.setSeqAfaTipoOcorSelecionada(dispMdto.getTipoOcorrenciaDispensacao().getSeq().toString());
			}
			dispMdto.setIndSituacaoNova(dispMdto.getIndSituacao());
			processaImagensSituacoesDispensacao(dispMdto);
			
			processarSinalizadorSaldoInsuficiente(dispMdto);
		}
		
		return listaTriagem;
	}

	public void processarSinalizadorSaldoInsuficiente(AfaDispensacaoMdtos dispMdto) {
		
		Long qtdeDisponivel = getEstoqueFacade().obterQtdeDispByUnfAndMaterial(dispMdto.getUnidadeFuncional().getSeq(), dispMdto.getMedicamento().getMatCodigo());
		if(qtdeDisponivel == null){
			qtdeDisponivel = 0l;
		}
		if ((dispMdto.getUnidadeFuncional().getControleEstoque() &&
				!(dispMdto.getQtdeSolicitada() == null || dispMdto.getQtdeSolicitada().intValue() == 0))
				&& qtdeDisponivel < dispMdto.getQtdeSolicitada().intValue()){
			dispMdto.setSaldoInsuficiente(Boolean.TRUE);
		}else{
			dispMdto.setSaldoInsuficiente(Boolean.FALSE);	
		}
		
	}

	private DominioCoresSituacaoItemPrescrito obterCorSituacaoItemPrescrito(
			AfaDispensacaoMdtos dispMdto) {
		if(!DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial().contains(dispMdto.getAtendimento().getOrigem())
				&& !DominioPacAtendimento.S.equals(dispMdto.getAtendimento().getIndPacAtendimento())) {
			return DominioCoresSituacaoItemPrescrito.VERMELHO;
		}
		
		if(DominioSituacaoItemPrescritoDispensacaoMdto.DG.equals(dispMdto.getIndSitItemPrescrito())
				|| DominioSituacaoItemPrescritoDispensacaoMdto.PG.equals(dispMdto.getIndSitItemPrescrito())
						|| DominioSituacaoItemPrescritoDispensacaoMdto.DI.equals(dispMdto.getIndSitItemPrescrito())
								|| DominioSituacaoItemPrescritoDispensacaoMdto.PI.equals(dispMdto.getIndSitItemPrescrito())) {
				return DominioCoresSituacaoItemPrescrito.AMARELO;
		}
		
		if(DominioSituacaoItemPrescritoDispensacaoMdto.IS.equals(dispMdto.getIndSitItemPrescrito())) {
				return DominioCoresSituacaoItemPrescrito.VERDE;
		}
		
		if(DominioSituacaoItemPrescritoDispensacaoMdto.EI.equals(dispMdto.getIndSitItemPrescrito())
								|| DominioSituacaoItemPrescritoDispensacaoMdto.EG.equals(dispMdto.getIndSitItemPrescrito())) {
				return DominioCoresSituacaoItemPrescrito.VERMELHO;
		}
		
		//Não haverá alteração da cor da linha
		return null;
	}

	
	private RealizarTriagemMedicamentosPrescricaoRN getRealizarTriagemMedicamentosPrescricaoRN(){
		return realizarTriagemMedicamentosPrescricaoRN;
	}
	
	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}

	/**
	 * Processa lista de AfaDispensacao, setando TipoOcorDisp quando tipoGeladeira
	 * Se algum registro da lista for alterado, retorna TRUE
	 * O retorno (true ou false) será utilizado para iniciar a tela com valorAlterado = retorno
	 * Caso exista algum tipoGeladeira, então o valor é alterado e a tela é iniciado setando que já houve modificação.
	 * @param listaTriagem
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public Boolean processaTipoOcorrenciaParaListaMedicamentosPrescricao(
			List<AfaDispensacaoMdtos> listaTriagem) throws ApplicationBusinessException {
		
		Boolean result = Boolean.FALSE;
		RealizarTriagemMedicamentosPrescricaoRN realizarTriRn = getRealizarTriagemMedicamentosPrescricaoRN();
		for(AfaDispensacaoMdtos dispMdto:listaTriagem){
			result = realizarTriRn.processaTipoOcorrenciaSeGeladeira(dispMdto);
		}
		return result;
	}

	public void realizaTriagemMedicamentoPrescricao(
			List<AfaDispensacaoMdtos> listaTriagemModificada,
			List<AfaDispensacaoMdtos> listaTriagemOriginal,
			String nomeMicrocomputador) throws BaseException {
		
		validaSeErrosDeAlteracoesPendentes(listaTriagemModificada);
		IFarmaciaFacade farmaciaFacade = getFarmaciaFacade();
		for(int i = 0; i< listaTriagemModificada.size(); i++){
			
			AfaDispensacaoMdtos admOld = listaTriagemOriginal.get(i);
			AfaDispensacaoMdtos admNew = listaTriagemModificada.get(i);
			
			if("".equals(admNew.getSeqUnidadeFuncionalSelecionada())){
				throw new ApplicationBusinessException(RealizarTriagemMedicamentosPrescricaoONExceptionCode.REALIZAR_TRIAGEM_FARMACIA_OBRIGATORIA);
			}
			//Altera a indSituação do registro, em caso de erro é necessário recuperar o setado
			DominioSituacaoDispensacaoMdto indSit = admNew.getIndSituacao();
			if(!indSit.equals(admNew.getIndSituacaoNova())){
				admNew.setIndSituacao(admNew.getIndSituacaoNova());
			}
			
			if (verificaSeTriagemSofreuAlteracao(admOld,admNew)) {
				try{
					farmaciaFacade.atualizaAfaDispMdto(admNew, admOld, nomeMicrocomputador);
					admNew.setIndSituacaoNova(DominioSituacaoDispensacaoMdto.valueOf(admNew.getIndSituacao().name()));
					processaImagensSituacoesDispensacao(admNew);
					admOld = CoreUtil.cloneBean(admNew);
					listaTriagemOriginal.set(i, admOld);
				}catch (ApplicationBusinessException e) {
					admNew.setIndSituacao(indSit);
					throw e;
				}
			}
		}
	}
	
	/**
	 * Como a tela permite modificações na table (modificações multiplas) 
	 * verifica se sobrou pendências.
	 * @param listaTriagemModificada 
	 * @throws ApplicationBusinessException 
	 */
	private void validaSeErrosDeAlteracoesPendentes(List<AfaDispensacaoMdtos> listaTriagemModificada) throws ApplicationBusinessException {
		for(AfaDispensacaoMdtos adm:listaTriagemModificada){
			if(adm.getUnidadeFuncional() == null && (adm.getSeqUnidadeFuncionalSelecionada() != null && !"".equals(adm.getSeqUnidadeFuncionalSelecionada().trim()))){
				throw new ApplicationBusinessException(RealizarTriagemMedicamentosPrescricaoONExceptionCode.NENHUM_VALOR_ENCONTRADO_FARMACIA, adm.getSeqUnidadeFuncionalSelecionada());
			}else{
				if(adm.getTipoOcorrenciaDispensacao() == null && (adm.getSeqAfaTipoOcorSelecionada() != null && !"".equals(adm.getSeqAfaTipoOcorSelecionada().trim()))){
					throw new ApplicationBusinessException(RealizarTriagemMedicamentosPrescricaoONExceptionCode.NENHUM_VALOR_ENCONTRADO_OCORRENCIA, adm.getSeqAfaTipoOcorSelecionada());
				}
			}
		}
		
	}

	private IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}
	/**
	 * Verifica se a triagem sofreu alteração na tela
	 * @param admOld
	 * @param admNew
	 * @return
	 */
	public boolean verificaSeTriagemSofreuAlteracao(
			AfaDispensacaoMdtos admOld, AfaDispensacaoMdtos admNew) {
		if(!CoreUtil.igual(admOld.getQtdeDispensada(), admNew.getQtdeDispensada())
			|| !CoreUtil.igual(admOld.getTipoOcorrenciaDispensacao(), admNew.getTipoOcorrenciaDispensacao())
			|| !CoreUtil.igual(admOld.getUnidadeFuncional(), admNew.getUnidadeFuncional())
			|| !CoreUtil.igual(admOld.getIndSituacao(), admNew.getIndSituacao()) 
			) {
			return true;
		}
		
		return false;
	}

	public void processaAfaTipoOcorBySeqInAfaDispMdto(AfaDispensacaoMdtos adm) throws ApplicationBusinessException {
		adm.setTipoOcorrenciaDispensacao(null);
		if(adm.getSeqAfaTipoOcorSelecionada() != null && !"".equals(adm.getSeqAfaTipoOcorSelecionada().trim())){
			List<AfaTipoOcorDispensacao> tiposOcorDispensacao = getFarmaciaDispensacaoFacade().pesquisarTipoOcorrenciasAtivasENaoEstornada();
			for(AfaTipoOcorDispensacao tod:tiposOcorDispensacao){
				if(tod.getSeq().toString().equals(adm.getSeqAfaTipoOcorSelecionada())){
					adm.setTipoOcorrenciaDispensacao(tod);
					return;
				}
			}
			adm.setTipoOcorrenciaDispensacao(null);
			throw new ApplicationBusinessException(RealizarTriagemMedicamentosPrescricaoONExceptionCode.NENHUM_VALOR_ENCONTRADO_OCORRENCIA, adm.getSeqAfaTipoOcorSelecionada());
		}
	}
	
	protected IFarmaciaDispensacaoFacade getFarmaciaDispensacaoFacade() {
		return this.farmaciaDispensacaoFacade;
	}

	public void processaAghUnfSeqInAfaDispMdto(AfaDispensacaoMdtos adm) throws ApplicationBusinessException {
		adm.setUnidadeFuncional(null);
		if(adm.getSeqUnidadeFuncionalSelecionada() != null && !"".equals(adm.getSeqUnidadeFuncionalSelecionada().trim())){
			List<AghUnidadesFuncionais> unfs = getFarmaciaDispensacaoFacade().listarFarmaciasAtivasByPesquisa("");
			for(AghUnidadesFuncionais unf:unfs){
				if(unf.getSeq().toString().equals(adm.getSeqUnidadeFuncionalSelecionada())){
					adm.setUnidadeFuncional(unf);
					return;
				}
			}
			adm.setUnidadeFuncional(null);
			throw new ApplicationBusinessException(RealizarTriagemMedicamentosPrescricaoONExceptionCode.NENHUM_VALOR_ENCONTRADO_FARMACIA, adm.getSeqUnidadeFuncionalSelecionada());
		}else{
			throw new ApplicationBusinessException(RealizarTriagemMedicamentosPrescricaoONExceptionCode.SELECAO_FARMACIA_OBRIGATORIA_REALIZAR_TRIAGEM);
		}
	}
	
	/**
	 * Valida as imagens que devem ser exibidas para o usuário ao selecionar 
	 * as possíveis seleções de IndSituacao de AfaDispensacaoMdto
	 * @param dispMdto
	 */
	public void processaImagensSituacoesDispensacao(
			AfaDispensacaoMdtos dispMdto) {
		
		if(DominioSituacaoDispensacaoMdto.E.equals(dispMdto.getIndSituacao())){
			dispMdto.setIndSituacoes(Arrays.asList(
							DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_DESABILITADO,
							DominioSituacaoDispensacaoMdtoImagens.TRIADO_DESABILITADO,
							DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_DESABILITADO,
							DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_DESABILITADO,
							DominioSituacaoDispensacaoMdtoImagens.ENVIADO_SELECIONADO));
		}else if(DominioSituacaoDispensacaoMdto.S.equals(dispMdto.getIndSituacao())){
			dispMdto.setIndSituacoes(Arrays.asList(
					dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.S) ? DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_SELECIONADO : DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_HABILITADO,
					dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.T) ? DominioSituacaoDispensacaoMdtoImagens.TRIADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.TRIADO_HABILITADO,
					dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.D) ? DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_HABILITADO,
					DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_DESABILITADO,
					DominioSituacaoDispensacaoMdtoImagens.ENVIADO_DESABILITADO));
		}else if(DominioSituacaoDispensacaoMdto.T.equals(dispMdto.getIndSituacao())){
			dispMdto.setIndSituacoes(Arrays.asList(
					dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.S) ? DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_SELECIONADO : DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_HABILITADO,
					dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.T) ? DominioSituacaoDispensacaoMdtoImagens.TRIADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.TRIADO_HABILITADO,
					dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.D) ? DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_HABILITADO,
					DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_DESABILITADO,
					DominioSituacaoDispensacaoMdtoImagens.ENVIADO_DESABILITADO));
		}else if(DominioSituacaoDispensacaoMdto.D.equals(dispMdto.getIndSituacao())){
			dispMdto.setIndSituacoes(Arrays.asList(
					//Início Melhoria #19063
						//dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.S) ? DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_SELECIONADO : DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_HABILITADO,
						//dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.T) ? DominioSituacaoDispensacaoMdtoImagens.TRIADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.TRIADO_HABILITADO,
						dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.S) ? DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_SELECIONADO : DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_DESABILITADO,
						dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.T) ? DominioSituacaoDispensacaoMdtoImagens.TRIADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.TRIADO_DESABILITADO,
					//Fim Melhoria #19063
					dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.D) ? DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_HABILITADO,
					//Início Melhoria 16491
							//dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.C) ? DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_HABILITADO,
							DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_DESABILITADO,
					//Fim Melhoria 16491
					DominioSituacaoDispensacaoMdtoImagens.ENVIADO_DESABILITADO));
		}else if(DominioSituacaoDispensacaoMdto.C.equals(dispMdto.getIndSituacao())){
			dispMdto.setIndSituacoes(Arrays.asList(
					DominioSituacaoDispensacaoMdtoImagens.SOLICITADO_DESABILITADO,
					DominioSituacaoDispensacaoMdtoImagens.TRIADO_DESABILITADO,
					//Início Melhoria 16491
							//dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.D) ? DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_HABILITADO,
							//dispMdto.getIndSituacaoNova().equals(DominioSituacaoDispensacaoMdto.C) ? DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_SELECIONADO: DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_HABILITADO,
							DominioSituacaoDispensacaoMdtoImagens.DISPENSADO_DESABILITADO,
							DominioSituacaoDispensacaoMdtoImagens.CONFERIDO_SELECIONADO,
					//Fim Melhoria 16491
					DominioSituacaoDispensacaoMdtoImagens.ENVIADO_DESABILITADO));
		}
	}

	/**
	 * 
	 * @param atdSeqPrescricao
	 * @param seqPrescricao
	 * @param proximoRegistro Define se é para próximo registro ou anterior
	 * @return
	 */
	public Integer processaProximaPrescricaoTriagemMedicamentoByProntuario(Integer atdSeqPrescricao, Integer seqPrescricao, Boolean proximoRegistro) {
		List<AfaDispensacaoMdtos> lista  = getAfaDispensacaoMdtosDAO().
			processaProximaPrescricaoTriagemMedicamentoByProntuario(
					atdSeqPrescricao, seqPrescricao,
					proximoRegistro,
					DominioSituacaoDispensacaoMdto.S,
					DominioSituacaoDispensacaoMdto.T,
					DominioSituacaoDispensacaoMdto.D,
					DominioSituacaoDispensacaoMdto.C,
					DominioSituacaoDispensacaoMdto.E);
		
		if(lista == null || lista.isEmpty()){
			return null;
		} else {
			return lista.get(0).getPrescricaoMedica().getId().getSeq();
		}
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	public Boolean verificarAcessoOcorrenciaDeTriagem(String computadorRede) {
		AghMicrocomputador micro = null;
		try {
			micro = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIPException(computadorRede);
		} catch (ApplicationBusinessException e) {
			return Boolean.FALSE;
		}
		if(micro == null || micro.getAghUnidadesFuncionais() == null){
			return Boolean.FALSE;
		}
		return getAghuFacade().unidadeFuncionalPossuiCaracteristica(micro.getAghUnidadesFuncionais().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}	
}
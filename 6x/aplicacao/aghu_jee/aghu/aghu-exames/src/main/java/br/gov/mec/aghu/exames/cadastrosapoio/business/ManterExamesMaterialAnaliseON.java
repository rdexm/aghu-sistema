package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvGrupoItemProcedId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author aghu
 *
 */
@Stateless
public class ManterExamesMaterialAnaliseON extends BaseBusiness {

private static final String _HIFEN_ = " - ";

private static final Log LOG = LogFactory.getLog(ManterExamesMaterialAnaliseON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = -1326358721993351267L;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;

	public enum ManterExamesMaterialAnaliseONExceptionCode implements
	BusinessExceptionCode {

		MENSAGEM_REQUERIDO_MATERIAL_ANALISE_TIPO_AMOSTRA_EXAMES, 
		MENSAGEM_REQUERIDO_MATERIAL_RECIPIENTE_TIPO_AMOSTRA_EXAMES, 
		AEL_00837, 
		AEL_00406, 
		FRM_40600, 
		AEL_00839, 
		AEL_00840,
		FAT_00073;

	}

	/**
	 * Aplica validações n formulário e regras de negócios ao adicionar itens na lista de tipos de amostra
	 * O objetivo é reduzir o uso sobrecarregado das RNs, contornar eventuais falhas de validação dos componentes 
	 * e evitar induzir o usuário ao erro.
	 * @return resultado das validações
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean validarAdicionarItemTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame, AelExamesMaterialAnalise examesMaterialAnalise, List<AelTipoAmostraExame> listaTiposAmostraExame) throws ApplicationBusinessException {

		// Valida campo obrigatório "Material" no slider de tipos de amostras de exame
		if (tipoAmostraExame.getMaterialAnalise() == null) {
			
			throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.MENSAGEM_REQUERIDO_MATERIAL_ANALISE_TIPO_AMOSTRA_EXAMES);
		
		}

		// Valida campo obrigatório "Recipiente" no slider de tipos de amostras de exame
		if (tipoAmostraExame.getRecipienteColeta() == null) {
			
			throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.MENSAGEM_REQUERIDO_MATERIAL_RECIPIENTE_TIPO_AMOSTRA_EXAMES);

		}

		// Valida campo "Número de amostras" no slider de tipos de amostras de exame
		Boolean isSolicInformaColetas = examesMaterialAnalise.getIndSolicInformaColetas();
		Boolean isGeraItemPorColetas = examesMaterialAnalise.getIndGeraItemPorColetas();
		Boolean isColetavel = tipoAmostraExame.getMaterialAnalise().getIndColetavel();
		Short nroAmostras =  tipoAmostraExame.getNroAmostras();

		if (!isSolicInformaColetas && !isGeraItemPorColetas && isColetavel && (nroAmostras == null || nroAmostras <= 0)) {

			throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.AEL_00837 , tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());

		}

		// Valida responsável pela coleta. Quanto for informada uma unidade funcional o responsável deve ser o coletador
		if ((!tipoAmostraExame.getResponsavelColeta().equals(DominioResponsavelColetaExames.C)) && (tipoAmostraExame.getUnidadeFuncional() != null)) {

			throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.AEL_00406, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());

		}

		// Validações através de comparações com os itens da lista
		if (listaTiposAmostraExame != null && !listaTiposAmostraExame.isEmpty()) {

			// Evita a ocorrência de itens duplicados na lista. Vide: Origem do atendimento e material de análise equivalentes
			DominioOrigemAtendimento origemAtendimento =  tipoAmostraExame.getOrigemAtendimento();
			AelMateriaisAnalises materiaisAnalise = tipoAmostraExame.getMaterialAnalise();

			for (AelTipoAmostraExame item : listaTiposAmostraExame) {

				if (item != tipoAmostraExame && origemAtendimento.equals(item.getOrigemAtendimento()) &&
						materiaisAnalise.getSeq().equals(item.getMaterialAnalise().getSeq())){

					throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.FRM_40600, tipoAmostraExame.getOrigemAtendimento().getDescricao(),tipoAmostraExame.getMaterialAnalise().getDescricao());

				}

			}

			// Valida origem do atendimento: Somente itens do tipo origem ou todas origens são permitidos.
			for (AelTipoAmostraExame item : listaTiposAmostraExame) {

				if ((origemAtendimento.equals(DominioOrigemAtendimento.T)) && (!origemAtendimento.equals(item.getOrigemAtendimento()))){
					
					throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.AEL_00839);

				}

				if ((!origemAtendimento.equals(DominioOrigemAtendimento.T)) && (item.getOrigemAtendimento().equals(DominioOrigemAtendimento.T))){
					
					throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.AEL_00840);

				}

			}	

		}

		return true;

	}
	
	public void adicionarProcedimentosRelacionados(
			List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO,		
			VFatConvPlanoGrupoProcedVO convenio, 
			FatItensProcedHospitalar fatItensProcedHospitalar,
			Short grcSeq) throws ApplicationBusinessException{
		
		if(listaFatProcedimentoRelacionadosVO != null && listaFatProcedimentoRelacionadosVO.size() > 0){
			for(VFatConvPlanoGrupoProcedVO vo : listaFatProcedimentoRelacionadosVO){
				if(vo.getCphCspCnvCodigo().equals(convenio.getCphCspCnvCodigo())
						&& vo.getFatItensProcedHospitalar().getSeq().equals(fatItensProcedHospitalar.getSeq())){
					
					throw new ApplicationBusinessException(ManterExamesMaterialAnaliseONExceptionCode.FAT_00073);
				}
			}
		}
		
		List<VFatConvPlanoGrupoProcedVO> novosItensParaAdicionar = this.getFaturamentoFacade().listarConveniosPlanos(grcSeq, fatItensProcedHospitalar.getId().getPhoSeq(), convenio.getCphCspCnvCodigo());
		
		for(int i = 0; i < novosItensParaAdicionar.size(); i++){
			novosItensParaAdicionar.get(i).setFatItensProcedHospitalar(fatItensProcedHospitalar);
			novosItensParaAdicionar.get(i).setOperacao(DominioOperacoesJournal.INS);
		}
		
		if(novosItensParaAdicionar.size() > 0){
			listaFatProcedimentoRelacionadosVO.addAll(novosItensParaAdicionar);
		}
		
	}
		
	public void salvarProcedimentosRelacionados(
			FatProcedHospInternos fatProcedHospInternos, 
			List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO,
			Short cpgGrcSeq, Short cpgCphPhoSeq) throws ApplicationBusinessException {
		
		
		
		if(listaFatProcedimentoRelacionadosVO != null && listaFatProcedimentoRelacionadosVO.size() > 0){
			StringBuffer mensagem = new StringBuffer(" Procedimento Integer "+fatProcedHospInternos.getSeq()+" foi associado aos seguintes Procedimentos Relacionados:");
			for(VFatConvPlanoGrupoProcedVO vo : listaFatProcedimentoRelacionadosVO){
				
				FatConvGrupoItemProcedId id = new FatConvGrupoItemProcedId();
				id.setIphPhoSeq(cpgCphPhoSeq);
				id.setIphSeq(vo.getFatItensProcedHospitalar().getSeq());
				id.setCpgCphCspCnvCodigo(vo.getCphCspCnvCodigo());
				id.setCpgCphCspSeq(vo.getCphCspSeq());
				id.setCpgCphPhoSeq(cpgCphPhoSeq);
				id.setCpgGrcSeq(cpgGrcSeq);
				id.setPhiSeq(fatProcedHospInternos.getSeq());
				
				FatConvGrupoItemProced fatConvGrupoItemProced = new FatConvGrupoItemProced();
				
				fatConvGrupoItemProced.setIndCobrancaFracionada(Boolean.FALSE);
				fatConvGrupoItemProced.setIndExigeJustificativa(Boolean.FALSE);
				fatConvGrupoItemProced.setIndImprimeLaudo(Boolean.FALSE);
				
				fatConvGrupoItemProced.setId(id);
				fatConvGrupoItemProced.setProcedimentoHospitalarInterno(fatProcedHospInternos);
				
				getFaturamentoFacade().persistirGrupoItemConvenio(fatConvGrupoItemProced, null, DominioOperacoesJournal.INS);
				
				mensagem.append(vo.getCnvDescricao())
					.append(_HIFEN_).append(vo.getCspDescricao())
					.append(_HIFEN_).append(vo.getFatItensProcedHospitalar().getDescricao());
			}
			AghParametros parametro= this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_NOTIFICACAO_VINCULACAO_PROCEDIMENTOS);
			List<RapServidores> listaRapServidores = new ArrayList<RapServidores>();
			if(parametro != null){
				listaRapServidores = this.getRegistroColaboradorFacade().listarUsuariosNotificaveis(parametro.getVlrNumerico().intValue());
			}
			
			this.gravarMensagemSucesso(mensagem.toString(), listaRapServidores);
		}
	}

	protected IFaturamentoFacade getFaturamentoFacade() { 
		return  this.faturamentoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() { 
		return  this.registroColaboradorFacade;
	}
	
	protected IParametroFacade getParametroFacade() { 
		return  this.parametroFacade;
	}
	
	protected ICentralPendenciaFacade getCentralPendenciaFacade() { 
		return  this.centralPendenciaFacade;
	}
	
	private void gravarMensagemSucesso(String mensagem, List<RapServidores> listaServidores) throws ApplicationBusinessException {
		getCentralPendenciaFacade().adicionarPendenciaInformacao(mensagem, listaServidores, Boolean.TRUE);
		 
	}

}

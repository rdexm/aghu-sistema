package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.vo.TicketMdtoDispensadoVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DispensacaoMdtosCodBarrasON extends BaseBusiness {


	@EJB
	private AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN;
	
	@EJB
	private DispensacaoMdtosCodBarrasRN dispensacaoMdtosCodBarrasRN;
	
	private static final Log LOG = LogFactory.getLog(DispensacaoMdtosCodBarrasON.class);
	
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
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	

	
	public enum DispensacaoMdtosCodBarrasONExceptionCode implements	BusinessExceptionCode {
		MICROCOMPUTADOR_NAO_DISPENSADOR, UNF_COMPUTADOR_DIFERE_UNF_MEDICAMENTO
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3996712000714809137L;

	public List<AfaDispensacaoMdtos> pesquisarListaDispMdtoDispensarPelaPrescricao(
			Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro) {
		
		if(unidadeFuncionalMicro == null){
			return new ArrayList<AfaDispensacaoMdtos>();
		}
		
		List<AfaDispensacaoMdtos> listaMdtosDispensar = getAfaDispensacaoMdtosDAO()
				.pesquisarListaDispMdtoDispensarPelaPrescricao(pmeAtdSeq,
						pmeSeq, unidadeFuncionalMicro);
		AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN = getAfaDispMdtoCbSpsRN();
		for (AfaDispensacaoMdtos dispMdto : listaMdtosDispensar) {
			dispMdto.setCorSinaleiro(afaDispMdtoCbSpsRN.preencheSinaleira(
					dispMdto.getSeq(), dispMdto.getQtdeDispensada()));
			dispMdto.setItemDispensadoSemEtiqueta(afaDispMdtoCbSpsRN
					.assinalaMedicamentoDispensado(dispMdto.getSeq()));
			dispMdto.setPermiteDispensacaoSemEtiqueta(getFarmaciaDispensacaoFacade().verificarPermissaoDispensacaoMdtoSemEtiqueta(dispMdto));
		}
		CoreUtil.ordenarLista(listaMdtosDispensar, "medicamento.descricaoEditada", false);
		CoreUtil.ordenarLista(listaMdtosDispensar, "corSinaleiro.codigo", true);
		return listaMdtosDispensar;
	}
	
	public void dispensarMdtoCodBarras(String nroEtiqueta, List<AfaDispensacaoMdtos> listaMdtosPrescritos, AghMicrocomputador microDispensador, String nomeMicrocomputador) throws BaseException, ApplicationBusinessException {
		getFarmaciaDispensacaoFacade().validaSeMicroComputadorDispensador(null, null,microDispensador);
		getDispensacaoMdtosCodBarrasRN().dispensarMdtoCodBarras(nroEtiqueta, listaMdtosPrescritos, microDispensador.getNome());
		processarAfaDispMdtoAposAtualizacaoComEtiqueta(listaMdtosPrescritos);
	}
	
	private void processarAfaDispMdtoAposAtualizacaoComEtiqueta(
			List<AfaDispensacaoMdtos> listaMdtosPrescritos) {
		for (AfaDispensacaoMdtos adm : listaMdtosPrescritos) {
			adm.setPermiteDispensacaoSemEtiqueta(getFarmaciaDispensacaoFacade()
					.verificarPermissaoDispensacaoMdtoSemEtiqueta(adm));
		}
	}
	
	public void assinaDispMdtoSemUsoDeEtiqueta(AfaDispensacaoMdtos admNew, String nomeMicrocomputador) throws BaseException {
		AghMicrocomputador microUserDispensador = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIP(nomeMicrocomputador, DominioCaracteristicaMicrocomputador.DISPENSADOR);
		getFarmaciaDispensacaoFacade().validaSeMicroComputadorDispensador(null, admNew, microUserDispensador);
		getEstoqueFacade().tratarDispensacaoMedicamentoEstoque(admNew, null, nomeMicrocomputador);
		getAfaDispMdtoCbSpsRN().assinaDispMdtoSemUsoDeEtiqueta(admNew, nomeMicrocomputador);
		admNew.setPermiteDispensacaoSemEtiqueta(Boolean.TRUE);
	}
		
	public Boolean verificaDispensacaoAntesDeEvento(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro) {
		
		Boolean possuiDisp = Boolean.FALSE;
		
		List<Long> seqDispMdtoList = getAfaDispensacaoMdtosDAO().pesquisarSeqsDispMdtoDispensarPelaPrescricao(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro);
		
		if (seqDispMdtoList != null && seqDispMdtoList.size() > 0) {
			possuiDisp = Boolean.TRUE;
		}
		
		return possuiDisp;
	}
	
	public List<TicketMdtoDispensadoVO> pesquisarDispensacaoMdto(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq) {

		List<TicketMdtoDispensadoVO> listRetorno = new ArrayList<TicketMdtoDispensadoVO>();
		listRetorno.addAll(getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtoSemCB(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro, pmmSeq));
		processarCoalesceSelect(listRetorno);
		
		Date dthrUltimoTicket = new Date();
		Object[] resultado = getAfaDispensacaoMdtosDAO().pesquisarMaxDataHrTicket(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro, pmmSeq);
		if(resultado != null){
			if(resultado[0] != null){
				dthrUltimoTicket = (Date) resultado[0]; 
			}else if(resultado[1] != null){
				dthrUltimoTicket = (Date) resultado[1]; 
			}
		}
		
		listRetorno.addAll(getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtoComCB(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro, pmmSeq, dthrUltimoTicket));
		listRetorno.addAll(getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtoComCBNaoImpresso(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro, pmmSeq, dthrUltimoTicket));
		
		for (TicketMdtoDispensadoVO mdtoDispensado : listRetorno) {
			String descricaoMdto = farmaciaFacade.obterMedicamento(mdtoDispensado.getMdtoCodigo()).getDescricaoEditada();
			mdtoDispensado.setMdtoDescricao(descricaoMdto);
			if(mdtoDispensado.getDthrTicket() == null){
				mdtoDispensado.setCheckboxReadonly(true);
				mdtoDispensado.setCheckboxSelecionado(true);
			}else{
				mdtoDispensado.setCheckboxReadonly(false);
				mdtoDispensado.setCheckboxSelecionado(false);
			}
			if(mdtoDispensado.getQtdUtilizadaLong() != null){
				mdtoDispensado.setQtdUtilizada(new BigDecimal(mdtoDispensado.getQtdUtilizadaLong()));
			}
			mdtoDispensado.setQuantidade(mdtoDispensado.getQtdUtilizada().setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString().replace("\\.0*$", "").replace('.', ','));
		}
		CoreUtil.ordenarLista(listRetorno, TicketMdtoDispensadoVO.Fields.DTHR_DISPENSACAO.toString(), false);
		return listRetorno;
	}
	
	private void processarCoalesceSelect(List<TicketMdtoDispensadoVO> listRetorno) {
		if(listRetorno != null){
			for (TicketMdtoDispensadoVO mdtoDispensado : listRetorno) {
				if(mdtoDispensado.getQtdeEstornada() != null){
					mdtoDispensado.setQtdUtilizada(mdtoDispensado.getQtdeDispensada().subtract(mdtoDispensado.getQtdeEstornada()));
				}else{
					mdtoDispensado.setQtdUtilizada(mdtoDispensado.getQtdeDispensada());
				}
			}
		}
	}	
	
	
	
	//Getters
	
	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
		return afaDispensacaoMdtosDAO;
	}

	private AfaDispMdtoCbSpsRN getAfaDispMdtoCbSpsRN() {
		return afaDispMdtoCbSpsRN;
	}
	
	private DispensacaoMdtosCodBarrasRN getDispensacaoMdtosCodBarrasRN() {
		return dispensacaoMdtosCodBarrasRN;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	protected IFarmaciaDispensacaoFacade getFarmaciaDispensacaoFacade() {
		return this.farmaciaDispensacaoFacade;
	}

	public Object[] obterNomeUnidFuncComputadorDispMdtoCb(AghUnidadesFuncionais unidadeFuncionalMicro, String nomeComputador) throws ApplicationBusinessException {
		AghMicrocomputador computador = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIPException(nomeComputador);			
		Object [] retorno = new Object [2];
		if(computador != null) {
			if (computador.getAghUnidadesFuncionais() != null){
				//return Arrays.asList(computador.getAghUnidadesFuncionais(), computador.getAghUnidadesFuncionais().getUnidadeDescricao());
				retorno[0] = computador.getAghUnidadesFuncionais();
				retorno[1] = computador.getAghUnidadesFuncionais().getUnidadeDescricao();
				
			}
			/*else{//Defeito em Desenvolvimento #16448
			retorno[1] = computador.getNome();
			}*/
		}/*else{
			return "";
		}*/
		return retorno;
	}
	
	public Date pesquisarMaxDataHrTicket(Integer atdSeqPrescricao, Integer seqPrescricao, AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq){
		Date retorno = null;
		Object[] resultado = getAfaDispensacaoMdtosDAO().pesquisarMaxDataHrTicket(atdSeqPrescricao, seqPrescricao, unidadeFuncionalMicro, pmmSeq);
		if(resultado != null){
			if(resultado[0] != null){
				retorno = (Date) resultado[0]; 
			}
		}
		return retorno;
	}
	
	public void refresh(List<AfaDispensacaoMdtos> listaDispensacao) {
		for(AfaDispensacaoMdtos adm : listaDispensacao){
			if(getAfaDispensacaoMdtosDAO().contains(adm)){
				getAfaDispensacaoMdtosDAO().refresh(adm);
			}
		}
	}	
	
	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}	

}